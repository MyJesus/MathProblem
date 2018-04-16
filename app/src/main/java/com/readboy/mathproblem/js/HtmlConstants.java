package com.readboy.mathproblem.js;

/**
 * Created by oubin on 2017/11/6.
 * div标签的style的宽高一定要填写完整，要不无法显示
 * <div style="height: 100%, width: 100%;overflow-y: auto;">
 */

interface HtmlConstants {
    static final String HTML_EXERCISE_SOLUTION_HEADER = "<html>\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=uft-8\">\n" +
            "    <style>\n" +
            "        body{\n" +
            "            font-size:25px;\n" +
            "            color:#000;\n" +
            "            font-weight:400;\n" +
            "        }\n" +
            "            *{ -webkit-tap-highlight-color:rgba(0,0,0,0); }" +
            "        p{\n" +
            "            padding:0 0 0 0;\n" +
            "        }\n" +
            "    </style>\n" +
            "    <script type=\"text/javascript\">\n" +
            "        function javacalljs(){\n" +
            "             document.getElementById(\"content\").innerHTML =\n" +
            "                 \"<br\\>JAVA调用了JS的无参函数\";\n" +
            "        }\n" +
            "\n" +
            "        function javacalljswith(arg){\n" +
            "             document.getElementById(\"content\").innerHTML =\n" +
            "                 (\"<br\\>\"+arg);\n" +
            "        }\n" +
            "    </script>\n" +
            "\n" +
            "    <script type='text/javascript'\n" +
            "            src='file:///android_asset/js/MathJax.js?config=TeX-AMS-MML_HTMLorMML'>\n" +
            "    </script>\n" +
            "    <script type='text/x-mathjax-config'>MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$'],\n" +
            "    ['\\\\\\\\(','\\\\\\\\)']]},'HTML-CSS': { preferredFont: 'TeX', availableFonts: ['TeX'], EqnChunk:\n" +
            "    80,EqnChunkDelay: 40, scale: 100},jax: ['input/TeX', 'output/HTML-CSS']});\n" +
            "    </script>\n" +
            "    <script type='text/x-mathjax-config'>\n" +
            "        MathJax.Hub.Register.MessageHook('TeX Jax - parse error', function (data) {});\n" +
            "    </script>\n" +
            "</head>";

    static final String HTML_EXERCISE_SOLUTION_FOOTER = "<script type=\"text/javascript\">\n" +
            "        var i=0;\n" +
            "        var dom=document.getElementsByTagName('tex')\n" +
            "        for(i;i<dom.length;i++){\n" +
            "            var src=dom[i].getAttribute('data-latex');\n" +
            "            dom[i].innerHTML='$'+src+'$';\n" +
            "        }\n" +
            "    window.onload=function(){\n" +
            "        MathJax.Hub.Queue(['Typeset', MathJax.Hub]);\n" +
            "    }\n" +
            "\n" +
            "</script>\n" +
            "</body>\n" +
            "</html>";

    String HTML_EXPLAIN_HEADER = "<html>\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=gb2312\">\n" +
            "    <style>\n" +
            "        body{\n" +
            "            font-size:22px;\n" +
            "            color:#000;\n" +
            "            font-weight:400;\n" +
            "            line-height:160%;" +
            "        }\n" +
            "        p{\n" +
            "            margin:0;\n" +
            "           padding:0 0 0 0;\n" +
            "        }\n" +
            "        ol>li{\n" +
            "          color:#000;\n" +
            "        }\n" +
            "    </style>\n" +
            "    <script type=\"text/javascript\">\n" +
            "        function javacalljs(){\n" +
            "             document.getElementById(\"content\").innerHTML =\n" +
            "                 \"<br\\>JAVA调用了JS的无参函数\";\n" +
            "        }\n" +
            "\n" +
            "        function javacalljswith(arg){\n" +
            "             document.getElementById(\"content\").innerHTML =\n" +
            "                 (\"<br\\>\"+arg);\n" +
            "        }\n" +
            "\n" +
            "    </script>\n" +
            "\n" +
            "    <script type='text/javascript'\n" +
            "            src='file:///android_asset/js/MathJax.js?config=TeX-AMS-MML_HTMLorMML'>\n" +
            "    </script>\n" +
            "    <script type='text/x-mathjax-config'>MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$'],\n" +
            "    ['\\\\\\\\(','\\\\\\\\)']]},'HTML-CSS': { preferredFont: 'TeX', availableFonts: ['TeX'], EqnChunk:\n" +
            "    80,EqnChunkDelay: 40, scale: 100},jax: ['input/TeX', 'output/HTML-CSS']});\n" +
            "\n" +
            "    </script>\n" +
            "    <script type='text/x-mathjax-config'>\n" +
            "        MathJax.Hub.Register.MessageHook('TeX Jax - parse error', function (data) {});\n" +
            "\n" +
            "    </script>\n" +
            "</head>\n" +
            "<body onselect=\"return false\">" +
            "<div style=\"height: 100%, width=100%;overflow-y: auto;\">";

    String HTML_EXPLAIN_FOOTER = "<div id=\"tag-height\" style=\"width: 100%, height: 1px;\"></div>\n" +
            "</div>" +
            "<script type=\"text/javascript\">\n" +
            "        var i=0;\n" +
            "        var dom=document.getElementsByTagName('tex')\n" +
            "        for(i;i<dom.length;i++){\n" +
            "            var src=dom[i].getAttribute('data-latex');\n" +
            "            dom[i].innerHTML='$'+src+'$';\n" +
            "        }\n" +
            "        var elems=document.getElementsByTagName('p')\n" +
            "        elems[0].style['padding-left']='45px';\n" +
            "        elems[0].style['padding-top']='1px';\n" +
            "        elems[0].style['font-size']='28px';\n" +
            "    window.onload=function(){\n" +
            "        MathJax.Hub.Queue(['Typeset', MathJax.Hub]);\n" +
            "        var elem=document.getElementById('tag-height');\n" +
            "        var height=elem.offsetTop;   //高度\n" +
            "        var width=elem.clientWidth;  //宽度\n" +
            "        var h=document.body.scrollHeight;\n" +
            "        window.android.onLoadFinish(height);" +
            "        setTimeout( 'getScroll()', 500)" +
            "    }\n" +
            "   function getScroll() {\n" +
            "       \tdocument.body.scrollTop=0;\n" +
            "       }" +
            "</script>\n" +
            "</body>\n" +
            "</html>";

    String HTML_EXAMPLE_HEADER = "<html>\n"+
            "<head>\n"+
            "    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=utf-8\">\n"+
            "    <style>\n"+
            "        body{\n"+
            "            font-size:22px;\n"+
            "            color:#000;\n"+
            "            font-weight:400;\n"+
            "            line-height:160%;" +
            "        }\n"+
            "        p{\n"+
            "            margin:0;\n"+
            "           padding:1px 0;\n"+
            "        }\n"+
            "        ol>li{\n"+
            "          color:#000;\n"+
            "        }\n"+
            "       @font-face{\n" +
            "           font-family: Round;\n" +
            "           src: url('file:///android_asset/js/fonts/FZY4JW.TTF');\n" +
            "        }" +
            "        .tips{\n"+
                "        margin-top: 53px;\n"+
            "        }\n"+
            "        .content{\n"+
            "           margin-left: 103px;"+
            "           font-size: 26px;" +
            "           margin-right: 166px;" +
            "        }\n"+
            "        .solution-tip,.answer-tip{\n"+
            "           cursor: pointer;\n"+
            "           display: inline-block;\n"+
            "           -webkit-tap-highlight-color:rgba(0,0,0,0);" +
            "           background-image: url('file:///android_asset/js/image/ic_expanded.png');\n"+
            "           margin-left: 45px;\n"+
            "           width: 42px;\n"+
            "           height: 42px;\n"+
            "        }\n"+
            "        .solution-tip-title,.answer-tip-title{\n"+
            "           -webkit-tap-highlight-color:rgba(0,0,0,0);" +
            "           position: absolute;\n"+
//            "           height: 42px;\n"+
            "           color: #f2af2c;\n"+
            "           cursor: pointer;\n"+
            "           margin-left: 18px;\n"+
            "           margin-right: 156px;" +
            "           line-height: 42px;\n"+
            "           font-weight: bold;" +
            "           font-size: 28px;" +
            "           font-style: bold;" +
//            "           font-family: Round;" +
            "        }\n"+
            "        .solution-detail,.answer-detail{\n"+
            "           margin-top: 48px;\n"+
            "           margin-left: 103px;\n"+
            "           margin-right: 156px;" +
//            "           font-weight: bold;" +
            "           font-size: 25px;" +
            "        }\n"+
            "    </style>\n"+
            "\n"+
            "    <script type='text/javascript'\n"+
            "            src='file:///android_asset/js/MathJax.js?config=TeX-AMS-MML_HTMLorMML'>\n"+
            "    </script>\n"+
            "    <script type='text/x-mathjax-config'>MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$'],\n"+
            "    ['\\\\\\\\(','\\\\\\\\)']]},'HTML-CSS': { preferredFont: 'TeX', availableFonts: ['TeX'], EqnChunk:\n"+
            "    80,EqnChunkDelay: 40, scale: 100},jax: ['input/TeX', 'output/HTML-CSS']});\n"+
            "\n"+
            "    </script>\n"+
            "    <script type='text/x-mathjax-config'>\n"+
            "        MathJax.Hub.Register.MessageHook('TeX Jax - parse error', function (data) {});\n"+
            "\n"+
            "    </script>\n"+
            "</head>\n"+
            "<body onselect=\"return false\">\n"+
            "<div style=\"height: 100%, width: 100%;overflow-y: auto;\">\n"+
            "\t<div class=\"content\">";

    String EXAMPLE_CONTENT_FOOTER = "</div>\n" +
            "<div class=\"solution tips\">\n" +
            "   <div style=\"position: relative;\" onclick=\"getSolution(1)\">\n" +
            "       <div class=\"solution-tip\" data-value=\"1\"></div>\n" +
            "       <span class=\"solution-tip-title\"><b>方法提示</b></span>\n" +
            "   </div>\t\n" +
            "   <div class=\"solution-detail\" style=\"display: none;\">";

    String EXAMPLE_SOLUTION_FOOTER = "</div>\n" +
            "\t\n" +
            "</div>\n" +
            "<div class=\"answer tips\">\n" +
            "\t<div style=\"position: relative;\" onclick=\"getSolution(2)\">\n" +
            "\t\t<div class=\"answer-tip\" data-value=\"1\"></div>\n" +
            "\t\t<span class=\"answer-tip-title\"><strong>答案</strong></span>\n" +
            "\t</div>\t\n" +
            "\t<div class=\"answer-detail\" style=\"display: none;\">";

    String HTML_EXAMPLE_FOOTER = "</div>\n" +
            "\t</div>\t\n" +
            "<div id=\"tag-height\" style=\"height: 100%, width: 100%;\"></div>\n" +
            "</div>\n" +
            "\n" +
            "<script type=\"text/javascript\">\n" +
            "        var i=0;\n" +
            "        var dom=document.getElementsByTagName('tex')\n" +
            "        for(i;i<dom.length;i++){\n" +
            "            var src=dom[i].getAttribute('data-latex');\n" +
            "            dom[i].innerHTML='$'+src+'$';\n" +
            "        }\n" +
            "    window.onload=function(){\n" +
            "        MathJax.Hub.Queue(['Typeset', MathJax.Hub]);\n" +
            "    }\n" +
            "    function getSolution(index){\n" +
            "       var obj='',obj_1='';\t\n" +
            "       if(index==1){\n" +
            "       obj='solution-tip';\n" +
            "       obj_1='solution-detail'\n" +
            "         }else{\n" +
            "       \tobj='answer-tip';\n" +
            "       \tobj_1='answer-detail'\n" +
            "         }\n" +
            "         var elem=document.getElementsByClassName(obj)[0];\n" +
            "         var elem_1=document.getElementsByClassName(obj_1)[0];\n" +
            "         var val=elem.getAttribute('data-value');\n" +
            "         if(val==1){\n" +
            "         \t \telem.style['background-image']='url(\"file:///android_asset/js/image/ic_collapsed.png\")'\n" +
            "         \t\telem.setAttribute('data-value',2);\n" +
            "         \t\telem_1.style.display='block';\n" +
            "         }else{\n" +
            "         \t \telem.style['background-image']='url(\"file:///android_asset/js/image/ic_expanded.png\")'\n" +
            "         \t\telem.setAttribute('data-value',1);\n" +
            "         \t\telem_1.style.display='none';\n" +
            "         }\n" +
            "        \n" +
            "    }\n" +
            "</script>\n" +
            "</body>\n" +
            "</html>";

}
