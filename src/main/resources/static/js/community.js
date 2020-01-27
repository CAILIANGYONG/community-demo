
function post() {
    var questionId =$("#question_id").val();
    var content = $("#comment_content").val();
    if(!content)//没有值为真
    {
        alert("不能回复空内容~~");
        return;
    }
    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId":questionId,
            "content":content,
            "type":1
        }),
        success: function (response) {
            if(response.code==200){
               // $("#comment_section").hide();
                window.location.reload();
            }
            else{
                if(response.code==2003)
                {
                    var isAccepted =confirm(response.message);  //没登录弹窗提示
                    if(isAccepted){//登录验证
                        window.open("https://github.com/login/oauth/authorize?client_id=fb5f8fdb1da299181263&redirect_uri=http://localhost:8886/callback&scope=user&state=1");
                        window.localStorage.setItem("closable",true);
                    }
                }else
                {
                alert(response.message);
            }
            }
            console.log(questionId);
            console.log(content);
            console.log(response);
        },
        dataType: "json"
    });

}