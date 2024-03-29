package com.cloud.console.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.console.Result;
import com.cloud.console.common.ExportUtils;
import com.cloud.console.common.Logger;
import com.cloud.console.po.Module;
import com.cloud.console.service.IndexService;
import com.cloud.console.service.ModuleService;
import com.cloud.console.service.Options;
import com.cloud.console.service.Paging;
import com.cloud.console.vo.TreeNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/** Created by Frank on 2017/8/3. */
@RestController
@RequestMapping("/module")
public class ModuleController {

  @Autowired RedisTemplate redisTemplate;
  @Autowired ModuleService moduleService;
  @Autowired IndexService indexService;
  @Autowired
  Logger logger;

  @GetMapping(value = "/isExist")
  public Result isExist(Module params) throws Exception {
    Result result = new Result();
    List<com.cloud.console.vo.Module> modules = moduleService.getModuleByName(params.getName());
    if (CollectionUtils.isEmpty(modules)) {
      result.setCode("1");
      result.setMsg("notExist");
    } else {
      result.setCode("0");
      result.setMsg("模块已存在");
    }
    return result;
  }

  @GetMapping(value = "/getModulesPaging")
  public Paging getModulesPaging(Integer limit, Integer offset, String name) throws Exception {
    return moduleService.getModules(limit, offset, name);
  }

  @GetMapping(value = "/getModules")
  @ResponseBody
  public List<Module> getModules() throws Exception {
    Paging paging = moduleService.getModules(null, null, null);
    return paging.getRows();
  }

  @PostMapping(value = "/add")
  public Result add(Module module) throws Exception {
    Result result = new Result();
    Long parentId = module.getParent_id();
    if (parentId != null && parentId > 0) {
      module.setIs_leaf(1);
    }
    moduleService.addModule(module);
    redisTemplate.opsForValue().set("menus", indexService.builderMenus());
    logger.log("编辑模块" + JSON.toJSONString(module));
    result.setCode("1");
    result.setCode("新增成功");
    return result;
  }

  @PatchMapping(value = "/update")
  public Result edit(Module module) throws Exception {
    Result result = new Result();
    Long parentId = module.getParent_id();
    if (parentId != null && parentId > 0) {
      module.setIs_leaf(1);
    }
    moduleService.updateModule(module);
    redisTemplate.opsForValue().set("menus", indexService.builderMenus());
    logger.log("编辑模块" + JSON.toJSONString(module));
    result.setCode("1");
    result.setMsg("修改成功");
    return result;
  }

  @DeleteMapping(value = "/del")
  public Result del(@RequestBody List<Module> modules) throws Exception {
    Result result = new Result();
    moduleService.delModule(modules);
    redisTemplate.opsForValue().set("menus", indexService.builderMenus());
    logger.log("删除模块" + JSON.toJSONString(modules));
    result.setCode("1");
    result.setCode("删除成功");
    return result;
  }

  @GetMapping(value = "/export")
  public Result export(@RequestParam LinkedHashMap<String, String> header) throws Exception {
    Result result = new Result();
    List<com.cloud.console.vo.Module> modules =
        moduleService.getModules(null, null, null).getRows();
    String fileUrl = ExportUtils.export("模块管理", header, modules);
    result.setCode("1");
    result.setMsg(fileUrl);
    return result;
  }

  @GetMapping(value = "/buildTree")
  public List<TreeNode> buildTree() throws Exception {
    List<TreeNode> trees = new ArrayList<>();
    TreeNode tree = moduleService.buildTree();
    trees.add(tree);
    return trees;
  }

  @GetMapping(value = "/getOptions")
  public Map<String, Object> getOptions(String moduleName) throws Exception {
    Map<String, Object> oMap = new HashMap<>();
    List<com.cloud.console.vo.Module> modules;
    if (StringUtils.isNotBlank(moduleName)) {
      modules = moduleService.getModuleByName(moduleName);
      if (modules.size() > 0) {
        String options = modules.get(0).getOptions();
        if (StringUtils.isNotBlank(options)) {
          String[] optionArray = options.split(",");
          StringBuilder stringBuilder = new StringBuilder();
          int i = 0;
          for (String option : optionArray) {
            stringBuilder
                .append(Options.getCodeByName(option))
                .append(i < optionArray.length - 1 ? "," : "");
            i++;
          }
          oMap.put("option", stringBuilder.toString());
        }
      }
    }
    List<Map<String, String>> options = new ArrayList<>();
    Map<String, String> map;
    for (Options option : Options.values()) {
      map = new HashMap<>();
      map.put("code", option.getCode());
      map.put("name", option.getName());
      options.add(map);
    }
    oMap.put("options", options);
    return oMap;
  }
}
