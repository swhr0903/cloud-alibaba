package com.cloud.console.controller;

import com.alibaba.fastjson.JSON;
import com.cloud.console.Result;
import com.cloud.console.common.Logger;
import com.cloud.console.po.Role;
import com.cloud.console.po.RoleAuth;
import com.cloud.console.service.IndexService;
import com.cloud.console.service.Paging;
import com.cloud.console.service.RoleService;
import com.cloud.console.vo.Auths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Created by Frank on 2017/8/3. */
@RestController
@RequestMapping(value = "/role")
public class RoleController {

  @Autowired RedisTemplate redisTemplate;
  @Autowired RoleService roleService;
  @Autowired IndexService indexService;
  @Autowired
  Logger logger;

  @GetMapping(value = "/isExist")
  public Result isExist(Role params) throws Exception {
    Result result = new Result();
    List<Role> role = roleService.getRoles(null, null, params.getCode(), null).getRows();
    if (role != null && role.size() > 0) {
      result.setCode("0");
      result.setMsg("角色已存在");
    } else {
      result.setCode("1");
      result.setMsg("notExist");
    }
    return result;
  }

  @GetMapping(value = "/getRolesPaging")
  public Paging getRolesPaging(Integer limit, Integer offset, String code, String name)
      throws Exception {
    return roleService.getRoles(limit, offset, code, name);
  }

  @GetMapping(value = "/getRoles")
  public List<Role> getRoles() throws Exception {
    Paging paging = roleService.getRoles(null, null, null, null);
    return paging.getRows();
  }

  @PostMapping(value = "/add")
  public Result add(Role role) throws Exception {
    Result result = new Result();
    roleService.addRole(role);
    redisTemplate.opsForValue().set("menus", indexService.builderMenus());
    logger.log("编辑角色" + JSON.toJSONString(role));
    result.setCode("1");
    result.setMsg("新增成功");
    return result;
  }

  @PatchMapping(value = "/update")
  public Result update(Role role) throws Exception {
    Result result = new Result();
    roleService.updateRole(role);
    redisTemplate.opsForValue().set("menus", indexService.builderMenus());
    logger.log("编辑角色" + JSON.toJSONString(role));
    result.setCode("1");
    result.setMsg("修改成功");
    return result;
  }

  @DeleteMapping(value = "/del")
  public Result del(@RequestBody List<Role> roles) throws Exception {
    Result result = new Result();
    roleService.delRole(roles);
    redisTemplate.opsForValue().set("menus", indexService.builderMenus());
    logger.log("删除角色" + JSON.toJSONString(roles));
    result.setCode("1");
    result.setMsg("删除成功");
    return result;
  }

  @PostMapping(value = "/auth")
  public Result auth(@RequestBody Auths auths) throws Exception {
    Result result = new Result();
    roleService.auth(auths);
    logger.log("角色授权" + JSON.toJSONString(auths.getPermissions()));
    result.setCode("1");
    result.setMsg("授权成功");
    return result;
  }

  @RequestMapping(value = "/getRoleAuths")
  @ResponseBody
  public List<RoleAuth> getRoleAuths(Long roleId) throws Exception {
    return roleService.getRoleAuths(roleId);
  }
}
