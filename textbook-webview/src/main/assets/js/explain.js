    $(document).ready(function() {
        $(".hl").on("click", function() {
            var rel = $(this).attr("id");
            JavaScriptObject.showComment(rel);
        });
	
        var fontSize = parseFloat($("p").css("font-size"));
		var deviceScale = parseFloat(JavaScriptObject.getDeviceScale());
	    $("img").each(function(){
            var w = $(this).width();
			var h = $(this).height();
			console.log("clientWidth="+document.body.clientWidth+"deviceScale="+deviceScale+"font"+fontSize);
			if(w > document.body.clientWidth )
			{
				h = h - (w-document.body.clientWidth)*h/w
				w = document.body.clientWidth;
			}
			else
			{
				if(fontSize &&  deviceScale)
				{
					console.log("img-h="+h+"img-w="+w);
					if(h < fontSize*deviceScale)
					{
						w = w + (fontSize - h)*w/h;
						h = fontSize+deviceScale;
					}
				}
				
			}
			$(this).css({
				width:w,
				height:h,
			});
        })
        
    })