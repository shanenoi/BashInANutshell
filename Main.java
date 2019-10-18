import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.*;

class Bash {

    //=========//Part of bash//=========//
    private String sourceCode = "";
    //=========//Lib default of bash//=========//
    class io {

        void echo(String opt, String content) {
            Bash.this.sourceCode += String.format(
                    "echo %s \"%s\";\n",
                    opt.equals("") ? "":"-"+opt,
                    Bash.this.changeString(content)
            );
        }

        void read(String opt, String out, String ...varName) {
            Bash.this.sourceCode += String.format(
                    "read -p%s \"%s\" %s;\n",
                    opt, Bash.this.changeString(out),
                    (
                            varName.length == 0 ?
                                    Arrays.toString(varName) :String.join(
                                    " ", varName
                            )
                    )
            );
        }
    }

    class array {
        void New(String varName, String type, String ...values) {
            if ("string".equals(type)) {
                for (int i=0; i<values.length; i++) {
                    values[i] = (
                            "\""+Bash.this.changeString(values[i])+"\""
                    );
                }
            }
            Bash.this.sourceCode += (
                    String.format(
                            "%s=(%s)",
                            varName, String.join(
                                    " ", values
                            )
                    )
            );
        }
    }
    //=========//Procedure of bash//=========//
    private String changeString(String text) {
        //=========//Variable//=========//
        Pattern pattern = Pattern.compile("(@[^@]+@)");
        Matcher matcher = pattern.matcher(text);
        String temp, temp1, temp2;
        while(matcher.find()) {
            temp = matcher.group();
            text = text.replace(
                    temp,
                    "$"+temp.substring(1,temp.length()-1)
            );
        }
        //=========//Array index//=========//
        pattern = Pattern.compile("<([^<>\\s]+)>\\^<([^<>\\s]+)>");
        matcher = pattern.matcher(text);
        while(matcher.find()) {
            temp = matcher.group();
            temp1 = matcher.group(1);
            temp2 = matcher.group(2);
            text = text.replace(
                    temp,
                    String.format(
                            "${%s[%s]}",
                            temp1, temp2
                    )
            );
        }
        return text;
    }
    void var(String type, String name, String value) {
        if ("num".equals(type)) {
            this.sourceCode += (
                    String.format(
                            "%s=%s;\n",
                            name, value
                    )
            );
        } else if ("string".equals(type)) {
            this.sourceCode += (
                    String.format(
                            "%s=\"%s\";\n",
                            name, this.changeString(value)
                    )
            );
        }
    }
    void constant(String type, String name, String value) {
        if ("num".equals(type)) {
            this.sourceCode += (
                    String.format(
                            "%s=%s;\nreadonly %s;\n",
                            name, value, name
                    )
            );
        } else if ("string".equals(type)) {
            this.sourceCode += (
                    String.format(
                            "%s=\"%s\";\nreadonly %s;\n",
                            name, this.changeString(value), name
                    )
            );
        }
    }
    //=========//=========//=========//
    String Code() {
        return this.sourceCode;
    }

}

public class Main {

    public static void main(String[] args) {
        Bash bash = new Bash();
        Bash.io IO = bash.new io();
        IO.echo("e", "hello @name@  <abc>^<123> <abc>^<#>");
        Bash.array array = bash.new array();
        array.New("abc", "num", "abc", "def");
        System.out.println(bash.Code());
    }

}
