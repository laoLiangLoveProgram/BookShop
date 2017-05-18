/**
 * Created by Administrator on 2017/5/17.
 */
$(function(){
    $("input[name=submit]").click(function(){
        alert(1111);
        $.ajax({
            type:"POST",
            url:"user/login.do",
            data:{
                username:$("input[name=username]").val(),
                password:$("input[name=password]").val()
            },
            success:function(response, status, xhr){
                console.log(response);
                var json = response;
                if(json.status == 0){
                    location.href = "index.jsp";
                }else{
                    $("#span_username").text(json.msg);
                }
            }
        });

    });
});
