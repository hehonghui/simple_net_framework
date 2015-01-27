<img src="http://avatar.csdn.net/blogpic/20150115161936875.jpg">      
# SimpleNet网络框架
  SimpleNet是一个简单的Android网络框架，该框架的结构类似Volley，该框架是为了让不太熟悉框架开发或者说不太了解Android网络编程的同学学习使用。它没有经过测试，因此不太建议运用在您的项目中。当然，如果你觉得没有什么问题的话也可以直接使用在你的项目中。该框架可以以并发的形式执行网络请求，并且将结果投递给UI线程。更多介绍请参考<a href="http://blog.csdn.net/column/details/simple-net.html" target="_blank">教你写Android网络框架</a>

  
## 使用示例
```java   

    // 1、构建请求队列
    RequestQueue queue = SimpleNet.newRequestQueue();  
  
	// 2、创建请求
    MultipartRequest multipartRequest = new MultipartRequest("你的url", new 	RequestListener<String>() {
                    @Override
                    public void onComplete(int stCode, String response, String errMsg) {
                        // 该方法执行在UI线程
                    }
                }); 
  
	// 3、添加各种参数
	// 添加header  
	multipartRequest.addHeader("header-name", "value");  
	  
	// 通过MultipartEntity来设置参数  
	MultipartEntity multi = multipartRequest.getMultiPartEntity();  
	// 文本参数  
	multi.addStringPart("location", "模拟的地理位置");  
	multi.addStringPart("type", "0");  
  
	Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thumb);  
	// 直接从上传Bitmap  
	multi.addBinaryPart("images", bitmapToBytes(bitmap));  
	// 上传文件  
	multi.addFilePart("imgfile", new File("storage/emulated/0/test.jpg"));  


	// 4、将请求添加到队列中  
	queue.addRequest(multipartRequest); 
	
	
	// 返回JSONObject的请求
	//        JsonRequest jsonRequest  = new JsonRequest(HttpMethod.GET, "服务器地址", new RequestListener<JSONObject>() {
	//
	//            @Override
	//            public void onComplete(int stCode, JSONObject response, String errMsg) {
	//                
	//            }
	//            
	//        }) ;
	 
```        

 最后，记得在Activity销毁时关闭消息队列。    
 
```java
queue.stop();
```            

