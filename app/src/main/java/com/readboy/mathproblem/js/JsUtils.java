package com.readboy.mathproblem.js;

import android.text.TextUtils;

import com.readboy.mathproblem.http.response.ProjectEntity;

/**
 * Created by oubin on 2017/11/3.
 */

public class JsUtils implements HtmlConstants {

    public static final String TEXT = "<p>（1）甲种相机：</p><p>2006年比2005年增长23－15＝8万台</p>" +
            "<p>2007年比2006年增长30－23＝7万台</p><p>2008年比2007年增长40－30＝10万台</p><p>乙种相机：</p>" +
            "<p>2006年比2005年增长18－10＝8万台</p><p>2007年比2006年增长25－18＝7万台</p>" +
            "<p>2008年比2007年增长45－25＝15万台</p><p>（2）45÷40＝<tex data-latex=\"\\dfrac {9} {8}\"></tex></p>" +
            "<p>（3）（15＋23＋30＋40）÷4＝27（万台）。</p>";

    public static final String MATH_JAX_CONFIG = "<script type='text/x-mathjax-config'>MathJax.Hub.Register.MessageHook('TeX Jax - parse error', function (data) {});</script>";
    public static final String MATH_JAX_JS = "<script type='text/javascript' src='file:///android_asset/js/MathJax.js?config=TeX-AMS-MML_HTMLorMML'></script><script type="
            + "'text/x-mathjax-config'>MathJax.Hub.Config({tex2jax: {inlineMath: [['$','$'], ['\\\\(','\\\\)']]},'HTML-CSS': { preferredFont: 'TeX', availableFonts: ['TeX'], EqnChunk: 80,EqnChunkDelay: 40, scale: 100},jax: ['input/TeX', 'output/HTML-CSS']});</script>"
            + MATH_JAX_CONFIG;

    public static final String HTML = "<html>";
    public static final String HTML_END = "</html>";
    public static final String HTML_HEAD = "<head>";
    private static final String SPACE = "&nbsp;";

    private static final String HTML_HEAD_START = "<html>\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=uft-8\">\n";

    private static final String HTML_HEAD_END = "    <script type=\"text/javascript\">\n" +
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

    private static final String HTML_COMMON_HEADER = "<html>\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html;charset=uft-8\">\n" +
            "    <style>\n" +
            "        body{\n" +
            "            font-size:25px;\n" +
            "            color:#000;\n" +
            "            font-weight:400;\n" +
            "        }\n" +
            "        p{\n" +
            "            padding:2px 0;\n" +
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

    private static final String HTML_COMMON_FOOTER = "<script type=\"text/javascript\">\n" +
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


    private JsUtils() throws IllegalAccessException {
        throw new IllegalAccessException("you can not create me!");
    }

    @Deprecated
    public static String makeBaseHtmlText2(String text) {
        StringBuilder builder = new StringBuilder();
        builder.append(HTML);
        builder.append(HTML_HEAD);
        builder.append(MATH_JAX_JS);
        builder.append(text);
        builder.append(HTML_END);
        return builder.toString();
    }

    public static String makeBaseHtmlText(String text) {
        StringBuilder builder = new StringBuilder(HTML_COMMON_HEADER);
        builder.append(text);
        builder.append(HTML_COMMON_FOOTER);
        return builder.toString();
    }

    @Deprecated
    public static String makeExplainHtmlText2(String data) {
        StringBuilder builder = new StringBuilder();
        builder.append(HTML_HEAD_START);
        builder.append(makeStyle("21", "000", "200", "0"));
        builder.append(HTML_HEAD_END);
        builder.append(SPACE).append(SPACE);
        builder.append(data);
        builder.append(HTML_COMMON_FOOTER);
        return builder.toString();
    }

    public static String makeExplainHtmlText(String explain) {
        return HTML_EXPLAIN_HEADER + explain + HTML_EXPLAIN_FOOTER;
    }

    /**
     * @param header 序号，如“例1”
     * @return html文本，webView加载。
     */
    public static String makeExampleHtmlText(String header, ProjectEntity.Project.Example example) {
        return HTML_EXAMPLE_HEADER + addHeaderText(header, example.getContent()) + EXAMPLE_CONTENT_FOOTER + example.getSolution()
                + EXAMPLE_SOLUTION_FOOTER + example.getAnswer() + HTML_EXAMPLE_FOOTER;
    }

    public static String makeExampleHtmlText(ProjectEntity.Project.Example example) {
        return makeExampleHtmlText(null, example);
    }

    public static String addHeaderText(String header, String html) {
        if (TextUtils.isEmpty(header)) {
            return html;
        }
        String newHtml;
        String pTag = "<p>";
        if (html.startsWith(pTag)) {
            newHtml = html.replaceFirst(pTag, pTag + header);
        } else if (html.startsWith("<p ")) {
            int index = html.indexOf(">");
            newHtml = html.substring(0, index + 1)
                    + header
                    + html.substring(index + 1, html.length());
        } else {
            newHtml = header + html;
        }
        return newHtml;
    }

    /**
     * @param fontSize   25
     * @param color      000
     * @param fontWeight 400
     * @param padding    1
     * @return html header style.
     */
    private static String makeStyle(String fontSize, String color, String fontWeight, String padding) {
        StringBuilder builder = new StringBuilder("<style>\n"
                + "body{\n");
        builder.append("font-size:" + fontSize + ";\n");
        builder.append("color:#" + color + ";\n");
        builder.append("font-weight:" + fontWeight + ";\n");
        builder.append("}\n");
        builder.append("p{ \n");
        builder.append("padding:" + padding + "px 0;\n");
        builder.append("}\n"
                + "    </style>\n");
        return builder.toString();
    }

    public static class Style {
        String fontSize;
        String color;
        String fontWeight;
        String padding;

        public String getFontSize() {
            return fontSize;
        }

        public void setFontSize(String fontSize) {
            this.fontSize = fontSize;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getFontWeight() {
            return fontWeight;
        }

        public void setFontWeight(String fontWeight) {
            this.fontWeight = fontWeight;
        }

        public String getPadding() {
            return padding;
        }

        public void setPadding(String padding) {
            this.padding = padding;
        }
    }

}
