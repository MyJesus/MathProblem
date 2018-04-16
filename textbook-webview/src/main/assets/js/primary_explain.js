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
					if(h < (fontSize+6)*deviceScale)
					{
						w = w + (fontSize - h)*w/h;
						h = fontSize+6;
					}
				}
				
			}
			$(this).css({
				width:w,
				height:h,
			});
        })
        
        $("button").on("click", function() {
            var id = $(this).attr("id");
            $(this).toggleClass("reading");
            JavaScriptObject.read(id);
        });
        
        
        
    })
    
    function resetReadButton(id)
    {
    	if(id)
    	{
    	 	$("#"+id).removeClass("reading");
    	}
    }
    
    function scrollDivToTop(id)
    {
    	var div = $("#div-"+id);
    	if(div)
    	{
    		console.log("id="+'#div-'+id+"offset="+div.offset().top);
    		$(window).scrollTop(div.offset().top);
    	}
    }
    
    function getDivPosition(id)
    {
    	var div = $("#div-"+id);
    	if(div)
    	{
    		console.log("id="+'#div-'+id+"offsetTop="+div.offset().top+"offsetHeight"+div.offset().height);   		
    		JavaScriptObject.scrollPositionToTop(div.offset().top+';'+0);
    	}
    }