package ting.utility;

import org.unix4j.Unix4j;
import org.unix4j.line.Line;
import org.unix4j.unix.Grep;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class Main {

    private final static String[] SRC_ITEM_NAME = {"\"restart\"", "\"cancel\""};
    private final static String[] TARGET_ITEM_NAME = {"\"global_action_power_off_restart\"", "\"global_action_power_off_cancel\""};

    private final static String SRC_RES_FOLDER = "/media/ting/Data_TPV_iXensor/codebase/alps/frameworks/base/packages/PrintSpooler/res";
    private final static String TARGET_RES_FOLDER = "/media/ting/Data_TPV_iXensor/codebase/alps/frameworks/base/core/res/res";

    private final static String[] LANG = {
            "values-fa/strings.xml",
            "values-fi/strings.xml",
            "values-fr/strings.xml",
            "values-fr-rCA/strings.xml",
            "values-gl-rES/strings.xml",
            "values-gu-rIN/strings.xml",
            "values-hi/strings.xml",
            "values-hr/strings.xml",
            "values-hu/strings.xml",
            "values-hy-rAM/strings.xml",
            "values-in/strings.xml",
            "values-is-rIS/strings.xml",
            "values-iw/strings.xml",
            "values-ja/strings.xml",
            "values-ka-rGE/strings.xml",
            "values-kk-rKZ/strings.xml",
            "values-km-rKH/strings.xml",
            "values-kn-rIN/strings.xml",
            "values-ko/strings.xml",
            "values-ky-rKG/strings.xml",
            "values-lo-rLA/strings.xml",
            "values-lt/strings.xml",
            "values-lv/strings.xml",
            "values-mk-rMK/strings.xml",
            "values-ml-rIN/strings.xml",
            "values-mn-rMN/strings.xml",
            "values-mr-rIN/strings.xml",
            "values-ms-rMY/strings.xml",
            "values-my-rMM/strings.xml",
            "values-my-rZG/strings.xml",
            "values-nb/strings.xml",
            "values-ne-rNP/strings.xml",
            "values-nl/strings.xml",
            "values-pa-rIN/strings.xml",
            "values-pl/strings.xml",
            "values-pt/strings.xml",
            "values-pt-rBR/strings.xml",
            "values-pt-rPT/strings.xml",
            "values-ro/strings.xml",
            "values-ru/strings.xml",
            "values-si-rLK/strings.xml",
            "values-sk/strings.xml",
            "values-sl/strings.xml",
            "values-sq-rAL/strings.xml",
            "values-sr/strings.xml",
            "values-sv/strings.xml",
            "values-sw/strings.xml",
            "values-ta-rIN/strings.xml",
            "values-te-rIN/strings.xml",
            "values-th/strings.xml",
            "values-tl/strings.xml",
            "values-tr/strings.xml",
            "values-uk/strings.xml",
            "values-ur-rPK/strings.xml",
            "values-uz-rUZ/strings.xml",
            "values-vi/strings.xml",
            "values-zh-rHK/strings.xml",
            "values-zu/strings.xml"
    };


    public static void main(String[] args) {
        // restart, cancel
        int i = 0;
        for (String item : SRC_ITEM_NAME) {
            for (String lang : LANG) {
                File file = new File(SRC_RES_FOLDER + "/" + lang);
                List<Line> lines = Unix4j.grep(Grep.Options.F, item, file). toLineList();
                String lineContent = lines.get(0).getContent(); // should be the only one item
                System.out.println(lineContent);

                // remove {msgid="2472034227037808749"}
                lineContent = lineContent.replaceFirst("\\s+msgid=\\\"[0-9]+\\\"", "");
                System.out.println(lineContent);

                // replace restart of global_action_power_off_restart
                lineContent = lineContent.replaceFirst(item, TARGET_ITEM_NAME[i]);
                System.out.println(lineContent);
                System.out.print("\n\n");

                /// ----------
                /// write file
                /// ----------
                file = new File(TARGET_RES_FOLDER + "/" + lang);
                if (!file.exists()) {
                    System.err.println(file +" isn't exist!");
                    continue;
                }
                try {
                    BufferedWriter buf_writer = new BufferedWriter(new FileWriter("tmp.xml"));
                    BufferedReader buf_reader = new BufferedReader(new FileReader(file));
                    String readLine = "";
                    while ((readLine = buf_reader.readLine()) != null) {
                        buf_writer.write(readLine);
                        buf_writer.newLine();
                        if (readLine.contains("\"global_action_power_off\"")) {
                            buf_writer.write(lineContent);
                            buf_writer.newLine();
                        }
                    }
                    buf_reader.close();
                    buf_writer.close();

                    /// ----------
                    /// replace file
                    /// ----------
                    Files.move(Paths.get("tmp.xml"), Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }
}