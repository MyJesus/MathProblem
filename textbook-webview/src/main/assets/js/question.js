$(document).ready(function() {
        //单选题点击选项A,B...
        $('[type="radio"]').on("click", function() {
            var id = this.id;
			if(id)
			{
			   // $("#"+id).removeClass("normal");
				var ids = id.split("-");

//				console.log("radio -- "+ids[0]);
//				console.log("radio -- "+ids[1]);

				//移除已经选择的选项
				$("[name='"+ids[0]+"']").removeClass("select");
				$("[name='"+ids[0]+"']").addClass("normal");

//				$(this).toggleClass("select");
                $("#"+ids[0]+'-'+ids[1]).toggleClass("select");
				$("#"+ids[0]).text("(" + ids[1] + ")");
				JavaScriptObject.setUserAnswer(ids[0], ids[1]);
			}
        });
        //点击选项内容
        $('[type="opt"]').on("click", function() {
            var id = this.id;
                if(id)
                {
                    var ids = id.split("-");

//                    console.log(ids[0]);
//                    console.log(ids[1]);
//                    console.log(ids[2]);

                    $("[name='"+ids[0]+"']").removeClass("select");
                    $("[name='"+ids[0]+"']").addClass("normal");

                    $("#"+ids[0]+'-'+ids[1]).toggleClass("select");
                    $("#"+ids[0]).text("(" + ids[1] + ")");
                    JavaScriptObject.setUserAnswer(ids[0], ids[1]);
                }
        });

        //多选题
        $('[type="checkbox"]').on("click", function() {
            var id = this.id;
			if(id)
			{
				var ids = id.split("-");
				var answer = '';
				if($(this).hasClass("select")){
				   $(this).removeAttr("checked");
				}else{
				   $(this).attr("checked", "checked");
				}
				$(this).toggleClass("select");
				$('[name='+ids[0]+']').each(function(){
					var check = $(this).attr("checked")
					if("checked" == $(this).attr("checked")){
					    answer += this.id.split("-")[1];
					}
			    });
				$("#"+ids[0]).text("(" + answer + ")");
				console.log(answer);
				JavaScriptObject.setUserAnswer(ids[0], answer);
			}
        });

		$('[type="mult-opt"]').on("click", function() {
            var id = this.id;
			if(id)
			{
				var ids = id.split("-");
				var answer = '';
				var choice = $("#"+ids[0]+'-'+ids[1]);
				if(choice.hasClass("select")){
				   choice.removeAttr("checked");
				}else{
				   choice.attr("checked", "checked");
				}
				choice.toggleClass("select");
				$('[name='+ids[0]+']').each(function(){
					var check = $(this).attr("checked")
					if("checked" == $(this).attr("checked")){
					    answer += this.id.split("-")[1];
					}
			    });
				$("#"+ids[0]).text("(" + answer + ")");
				JavaScriptObject.setUserAnswer(ids[0], answer);
			}
        });

/*        
		$('[input_type="blank"]').on("blur", function() {
            var id = this.id;
			if(id)
			{
				var answer = $("#"+id).val();
				JavaScriptObject.setBlankUserAnswer(id, answer);
			}
        });
*/        
        
/*        $('div [contentEditable]').on("blur", function() {
            var id = $(this).attr("id");
			if(id)
			{
				var text = this.textContent;
				var answer = text.trim();
				JavaScriptObject.setUserAnswer(id, answer);
			}
        });
*/        
        $('[input_type="judge"]').on("click", function() {
            var id = this.id;
			if(id)
			{
				var ids = id.split("-");
				var text = "";
				if(ids[1] == "right")
				{
					text = "正确";
				}
				else if(ids[1] == "wrong")
				{
					text = "错误";
				}
				$("#"+ids[0]).text("( " + text + " )");
				JavaScriptObject.setUserAnswer(ids[0], ids[1]);
			}
        });
        
        $('[button_type="show_and_hide"]').on("click", function() {
            var id = this.id;
			if(id)
			{
				var ids = id.split("-");
				$("#"+ids[0]+'-explain-div').toggle('fast');
			}
        });
        
        $("[href='#']").on("click", function() {
            var index = $(this).attr("data-ref");
            var questionId = $("[qustion_type='0']").attr('id');
			if(index && questionId)
			{		
				var childrenQuestionId = JavaScriptObject.getChildrenQuestionId(questionId, index);
				var checkedId = '';
				$('#'+childrenQuestionId+'-div '+'[name='+childrenQuestionId+']:checked').each(function(){
					checkedId = this.id;
			    });
				popCenterWindow(childrenQuestionId, checkedId);
			}
        });

		function popCenterWindow(questionId, checkedId){ 
			var html = $('#'+questionId+'-div').html();
			$('#popup_div').html(html);
			$("#popup_div").css("top",0).css("left",0).toggle();
			$('#popup_div').on("click",'[type="radio"]',function() {
				var id = $(this).attr("id");
				if(id)
				{
					var ids = id.split("-");
					$("span[id='"+questionId+"']").text("("+this.value+")");
					JavaScriptObject.setUserAnswer(ids[0], ids[1]);
					$("#popup_div "+'[name='+questionId+']:checked').each(function(){
						$("span[id='"+questionId+"']").text("("+this.value+")");
						$('#'+questionId+"-div input[id='"+questionId+'-'+this.value+"']").prop("checked",'true');
					});
					$('#popup_div').hide("slow"); 
				}
			});
			$("#popup_div input[id='"+checkedId+"']").prop("checked",'true');
		}
/*		
		$('img').load(function(){
			var w = $(this).width();
			var h = $(this).height();
			console.log("h="+h+"w="+w);
			if(w *3 > 1000)
			{
				w= 1000/3;
			}
			else
			{
				w= w/3;
				h = h/3;
			}
			$(this).css({
				width:w,
				height:h,
			});

		});
*/		
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
        
 /*      $('label').on("click", function() {
			$(this).css("background-color", "yellow");
        });
        $('label').mouseenter(function(){
        	$(this).css("background-color", "red");
        });
		$('label').mouseleave(function(){
        	$(this).css("background-color", "white");
        });
*/
	})

/*	
	$(window).unload(function() {
        console.log("unload.....");
		getEditeDivContent();
		getBlankInputContent();
	})
*/
		
	$(window).blur(function() {
		console.log("onblur.....");
		var timestamp=new Date().getTime();
		console.log("time="+timestamp);
		getEditeDivContent();
		getBlankInputContent();
		timestamp=new Date().getTime();
		console.log("time="+timestamp);
	})
	
	function getEditeDivContent(){
		var answers = new Array();
		var questionIds = new Array();
		$('div [contentEditable]').each(function(){
			var id = this.id
			if(id)
			{
				var text = this.textContent;
				var ids = id.split("-");
				questionIds[ids[1]] = ids[0];
				answers[ids[1]] = text;
			}
		});
		if(questionIds.length && answers.join("") !== "")
		{
			JavaScriptObject.setBlankUserAnswer(questionIds, answers);
		}
		
	}
		
	function getBlankInputContent(){
		var answers = new Array();
		var questionIds = new Array();
		$("[input_type='blank']").each(function(){
			var id = this.id
			if(id)
			{
				var text = this.value;
				var ids = id.split("-");
				questionIds[ids[1]] = ids[0];
				text = text.replace(/(^\s*)|(\s*$)/g, "");
				answers[ids[1]] = text;

			}
		});
		if(questionIds.length && answers.join("") !== "")
		{
			JavaScriptObject.setBlankUserAnswer(questionIds, answers);
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
	
	function setUserAnswer(){
		console.log("call by java.....");
		var timestamp=new Date().getTime();
		console.log("time="+timestamp);
		getEditeDivContent();
		timestamp=new Date().getTime();
		console.log("time="+timestamp);
		getBlankInputContent();
		timestamp=new Date().getTime();
		console.log("time="+timestamp);
	}