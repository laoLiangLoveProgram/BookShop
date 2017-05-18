/**
 * Created by Administrator on 2017/5/17.
 */
$(function () {
    $.ajax({
        type:"GET",
        url:"user/get_information.do",
        async:false,
        success: function (response) {
            if(response.status == 10){
                location.href = "login.jsp";
            }
        }
    });
});