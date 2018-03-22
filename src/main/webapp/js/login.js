function login() {
    $.ajax({
        type: "POST",
        url: "user/login.do",
        data: {
            username: $("input[name=username]").val(),
            password: $("input[name=password]").val()
        },
        success: function (response) {
            console.log(response);
            var json = response;
            if (json.status == 0) {
                location.href = "../index.jsp";
            } else {
                $("#span_username").text(json.msg);
            }
        }
    });
}
$(function () {
    $("input").keyup(
        function enterPress(e) {
            if (e.keyCode == 13) {
                login();
            }
        });
    $("input[name=submit]").click(login);
});