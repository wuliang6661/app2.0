package synway.module_publicaccount.analytical.fac;

class UrlDeal {

	//http://172.16.1.117:8081/ZKService/main/myDocument.html
	static final String getUrl(String url, String ip, int port){
		if(null == url || "".equals(url.trim())){
			return url; 
		}
		int startIndex = url.indexOf("/ZKService");
		if(startIndex != -1){
			//得到结果   /ZKService/main/myDocument.html
			String temp = url.substring(startIndex);
			String result = "http://" + ip + ":" + port + temp;
			return result;
		}
		return url;
	}

}