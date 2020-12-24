$(function(){
// 评论部分
  $("#button-submit").click(function(){
    // 获取序列化的html表单数据
    var data = new FormData($("#commentform")[0]); 
    var article_id = $("#id_article_id").attr("value") ;
    $.ajax({ 
      type:'post',  
      url:"/comments/post",  // 你请求的url  需要自己修改哦
      cache: false,    //上传文件不需缓存
      processData: false, //需设置为false。因为data值是FormData对象，不需要对数据做处理
      contentType: false, //需设置为false。因为是FormData对象，且已经声明了属性enctype="multipart/form-data"
      data:data,  // 表单数据
      dataType:'json', 
      success:function(data){  
          // 在这里可以做一些服务程序 ，
          // 比如从服务器接收json格式数据 ，解析，操作html显示数据
          if(data['state'] == "success"){
            $.get("/comments/search",
            { "article_id" : article_id },
              function(data){
                  if(data['state'] == "success"){
                    $("#comment-text").html("");
                    $("#comment-text").html(data["comment"]);
                  }
                  else if(data['state'] == "error"){
                    alert(data['info']);
                  }
              });
          }
          else if(data['state'] == "error"){
            alert(data['info']);
          }
          
        },
      error:function(){ 
          // 请求失败提示工作
          alert("请求失败");
        }
      });
  });
});

$(function(){
   // 取消评论
    $("#button-cancel").click(function(){
        $("#comment-label").html("评论")
        $("#id_parent_comment_id").attr("value", "")
        $(this).css("display","none")
      });

});

function reply_function(id){
    // 回复评论相关的函数  js
    var awards = $("#comment-input").offset.top;
    $('html,body').animate({scrollTop:awards}, 'slow');
    var author_id = "#author-"+id;
    var name = $(author_id).text();
    context = "回复"+"<span style='color: crimson;'>"+name+"</span>的评论";
    $("#comment-label").html(context)
    $("#button-cancel").css("display","")
    $("#id_parent_comment_id").attr("value", id)
}