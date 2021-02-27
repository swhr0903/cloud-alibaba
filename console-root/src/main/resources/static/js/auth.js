$(function () {
    $('#login').click(function () {
        var publicKey = getPublicKey($('#username').val());
        if (publicKey != null) {
            login(publicKey);
        }
    });

    $('#register').click(function () {
        if ($('#confirmPassword').val() != $('#passwordRegi').val()) {
            $('#warnModal').find('.modal-body').text("密码前后不一致");
            $('#warnModal').modal('show');
            return;
        }
        var publicKey = getPublicKey($('#usernameRegi').val());
        register(publicKey);
    });

    function getPublicKey(uaerName) {
        var publicKey;
        $.ajax({
            url: '/encrypt/getPublicKey/' + uaerName,
            type: 'get',
            async: false,
            contentType: "json",
            success: function (result) {
                if (typeof result == "undefined" || result == null || result == "") {
                    $('#warnModal').find('.modal-body').text("请求异常！");
                    $('#warnModal').modal('show');
                } else {
                    publicKey = result;
                }
            }
        });
        return publicKey;
    }

    function login(publicKey) {
        var encrypt = new JSEncrypt();
        encrypt.setPublicKey(publicKey);
        var encrypted = encrypt.encrypt($('#password').val());
        $.ajax({
            crossDomain: true,
            url: '/auth',
            data: {'username': $('#username').val(), 'password': encrypted,},
            type: 'post',
            async: false,
            success: function (result) {
                oauthAuth();
            },
            error: function () {
                $('#warnModal').find('.modal-body').text("帐号或密码错误");
                $('#warnModal').modal('show');
            }
        });
    }

    function oauthAuth() {
        $.ajax({
            url: '/oauth/authorize?client_id=web&response_type=code&redirect_uri=http://www.frank.com/oauth/callback&state=cn',
            type: 'post',
            async: false,
            success: function (result) {
                if (typeof result == "undefined" || result == null || result == "") {
                    $('#warnModal').find('.modal-body').text("请求异常！");
                    $('#warnModal').modal('show');
                } else {
                    getToken(result);
                }
            }
        });
    }

    function getToken(params) {
        var client_id = params.client_id;
        var client_secret = params.client_secret;
        var authorization_code = params.code;
        $.ajax({
            url: 'http://www.frank.com/oauth/token?grant_type=authorization_code&client_id=' + client_id + '&client_secret=' + client_secret +
                '&code=' + authorization_code + '&redirect_uri=http://www.frank.com/oauth/callback',
            type: 'post',
            async: false,
            contentType: "json",
            success: function (result) {
                if (typeof result == "undefined" || result == null || result == "") {
                    $('#warnModal').find('.modal-body').text("请求异常！");
                    $('#warnModal').modal('show');
                } else {
                    locationIndex(result.data.accessToken);
                    //window.location.href = '/index/?access_token=' + result.data.accessToken;
                }
            }
        });
    }

    function locationIndex(access_code) {
        $.ajax({
            type: "GET",
            url: "/",
            beforeSend: function (request) {
                request.setRequestHeader("Authorization", "Bearer " + access_code);
            }
        });
    }

    function register(publicKey) {
        var encrypt = new JSEncrypt();
        encrypt.setPublicKey(publicKey);
        var encrypted = encrypt.encrypt($('#passwordRegi').val());
        var user = {
            'username': $('#usernameRegi').val(),
            'name': $('#name').val(),
            'email': $('#email').val(),
            'password': encrypted,
            'confirmPassword': $('#confirmPassword').val()
        };
        $.ajax({
            url: '/manage/user/register',
            data: user,
            type: 'post',
            dataType: "json",
            success: function (result) {
                if (result.code == "1") {
                    $('#regiTip').text(result.msg);
                    $('#username').val($('#usernameRegi').val());
                    $('#password').val($('#passwordRegi').val());
                    setTimeout(function () {
                        login(publicKey);
                    }, 1000);
                } else {
                    $('#regiTip').text(result.msg);
                }
            },
            error: function () {
                $('#warnModal').find('.modal-body').text("帐号或密码错误");
                $('#warnModal').modal('show');
            }
        });
    }

    $('#secondAuth').click(function () {
        var username = $('#username').val();
        var password = $('#password').val();
        if (username == '' || password == '') {
            $('#warnModal').find('.modal-body').text("帐号密码不能为空");
            $('#warnModal').modal('show');
            return;
        }
        $.ajax({
            url: '/getAuthConfig',
            data: {'username': username, 'password': password},
            type: 'post',
            success: function (result) {
                var code = result.code;
                if (code == '1') {
                    var url = result.msg;
                    var win = window.open(url, '_blank');
                    win.focus();
                } else {
                    $('#warnModal').find('.modal-body').text(result.msg);
                    $('#warnModal').modal('show');
                }
            },
            error: function () {
                $('#warnModal').find('.modal-body').text("帐号或密码错误");
                $('#warnModal').modal('show');
            }
        });
    });

    $(document).keydown(function (event) {
        if (event.keyCode == 13) {
            $('#login').click();
        }
    });

    $('#registerForm').bootstrapValidator({
        message: 'This value is not valid',
        feedbackIcons: {
            valid: 'glyphicon glyphicon-ok',
            invalid: 'glyphicon glyphicon-remove',
            validating: 'glyphicon glyphicon-refresh'
        },
        fields: {
            username: {
                validators: {
                    notEmpty: {
                        message: '帐号不能为空'
                    }
                }
            },
            name: {
                validators: {
                    notEmpty: {
                        message: '姓名不能为空'
                    }
                }
            },
            email: {
                validators: {
                    notEmpty: {
                        message: '邮箱是紧急情况下你找回密码的唯一途径，不能为空'
                    },
                    emailAddress: {
                        message: '邮箱格式无效'
                    }
                }
            },
            password: {
                validators: {
                    notEmpty: {
                        message: '密码不鞥为空'
                    },
                    identical: {
                        field: 'confirmPassword',
                        message: '密码与确认密码不一致'
                    },
                    different: {
                        field: 'username',
                        message: '密码不能与帐号相同'
                    }
                }
            },
            confirmPassword: {
                validators: {
                    notEmpty: {
                        message: '确认密码不能为空'
                    },
                    identical: {
                        field: 'password',
                        message: '密码与确认密码不一致'
                    },
                    different: {
                        field: 'username',
                        message: '密码不能与帐号相同'
                    }
                }
            }
        }
    });

    $('#usernameRegi').change(function () {
        $.ajax({
            url: '/manage/user/isExist/' + $('#usernameRegi').val(),
            type: "get",
            contentType: 'application/json',
            dataType: "json",
            success: function (result) {
                if (result.code == '1') {
                    $('#register').prop('disabled', false);
                    $('#existTip2').html('');
                } else {
                    $('#existTip2').html(result.msg);
                    $('#usernameRegi').focus();
                    $('#register').prop('disabled', true);
                }
            }
        });
    });

    $('#reset').click(function () {
        $('#registerForm').data('bootstrapValidator').resetForm(true);
    });
});

