package org.crashxun.player.xunxun.subtitle.ass;

import android.text.TextUtils;
import android.util.Log;

import org.crashxun.player.xunxun.subtitle.DateUtil;
import org.crashxun.player.xunxun.subtitle.api.AbstractSubtitleParser;
import org.crashxun.player.xunxun.subtitle.api.SubtitleEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//@formatter:off

//@formatter:on
public class AssSubtitleParser extends AbstractSubtitleParser implements AssSubtitleDefine {
    private static final String ErrorMultipleTag = "multiple tag";
    private static final String ErrorNoFormat = "no format";
    private static final String ErrorFormatCountNotMatchValColCount = "format count not match value column count";


    @Override
    protected List<SubtitleEvent> convertToEvent(String allcontent) {
        Log.d(TAG, "allcontent:" + allcontent);
        if (TextUtils.isEmpty(allcontent)) {
            onParseFailer("subtitle content:null");
            return null;
        }
        //unify various 'enter' character, clear 'utf-16' serial character
        allcontent = allcontent.replaceAll("\r\n", "\n").replaceAll("\ufeff", "");

        List<SubtitleEvent> ret = null;
        //fetch 3 S,V4,E tag content java object
        AssTag assTag = getAssTag(allcontent);
        try {
            if (assTag != null) {
                ret = new ArrayList<>();
                //获取全局宽高
                int subScreenWidth = string2Int(assTag.getS().getPlayResX());
                int subScreenHeight = string2Int(assTag.getS().getPlayResY());

                SubtitleEvent subtitleEvent = null;
                AssTagEvent.Dialogue dialogue = null;
                AssTagV4Style.Style style = null;
                int index = 0;
                for (int i = 0; i < assTag.getE().getDialogues().size(); i++) {
                    dialogue = assTag.getE().getDialogues().get(i);
                    //目前先1个Dialogue = 1个事件
                    subtitleEvent = new SubtitleEvent();
                    subtitleEvent.setBaseScreenWidth(subScreenWidth);
                    subtitleEvent.setBaseScreenHeight(subScreenHeight);

                    //字大小,颜色,粗,斜,下划线,删除线,间距,角度
                    style = findStyle(assTag.getV(), dialogue.getStyle());
                    subtitleEvent.setFontName(style.getFontname());
                    subtitleEvent.setFontSize(string2Int(style.getFontsize()));
                    subtitleEvent.setPrimaryColor(getColor(style.getPrimaryColour()));
                    subtitleEvent.setSecondColor(getColor(style.getSecondaryColour()));
                    subtitleEvent.setShadowColor(getColor(style.getBackColour()));
                    subtitleEvent.setBold(isStyleOpen(style.getBold()));
                    subtitleEvent.setItalic(isStyleOpen(style.getItalic()));
                    subtitleEvent.setUnderline(isStyleOpen(style.getUnderline()));
                    subtitleEvent.setStrikeOut(isStyleOpen(style.getStrikeOut()));
                    subtitleEvent.setSpacing(string2Int(style.getSpacing()));
                    subtitleEvent.setAngle(string2Float(style.getAngle()));
                    //scaleX,scaleY
                    //...

                    //边框,颜色
                    subtitleEvent.setBorderColor(getColor(style.getOutlineColour()));
                    subtitleEvent.setBorderStyle(string2Int(
                            style.getBorderStyle()) == 1 ?
                            SubtitleEvent.BorderStyle.ShadowBorder
                            : SubtitleEvent.BorderStyle.NormalBorder);
                    subtitleEvent.setBorderWidth(string2Int(style.getOutline()));
                    subtitleEvent.setBorderShadowWidth(string2Int(style.getShadow()));
                    subtitleEvent.setAlignment(getAligment(style.getAlignment()));


                    //获取字位置
//                subtitleEvent.setPosiX();
//                subtitleEvent.setPosiY();
                    subtitleEvent.setMarginLeft(string2Int(style.getMarginL()));
                    subtitleEvent.setMarginRight(string2Int(style.getMarginR()));
                    subtitleEvent.setMarginVertical(string2Int(style.getMarginV()));

                    //获取时间
                    subtitleEvent.setStartTimeText(dialogue.getStart());
                    subtitleEvent.setEndTimeText(dialogue.getEnd());
                    subtitleEvent.setStartTimeMilliSec(DateUtil.convertTimeNoYMD2ms2(dialogue.getStart() + "0"));
                    subtitleEvent.setEndTimeMilliSec(DateUtil.convertTimeNoYMD2ms2(dialogue.getEnd() + "0"));
                    subtitleEvent.setDuringMilliSec(subtitleEvent.getEndTimeMilliSec()-subtitleEvent.getStartTimeMilliSec());

                    //获取每行文字
                    subtitleEvent.setText(dialogue.getText().replaceAll("[{]","")
                    .replaceAll("[}]","")
                    .replaceAll("\\\\",""));
                    //动画
                    //...

                    subtitleEvent.setIndex(index++);
                    ret.add(subtitleEvent);

                    int percent = (int) (i * 100f / assTag.getE().getDialogues().size() - 20);
                    onParseLoading(path, percent < 0 ? 0 : percent);
                    Log.d(TAG, "convertToEvent i:" + i + " subtitleEventItem:" + subtitleEvent);
                }
                //排序
                //...
                Collections.sort(ret, new Comparator<SubtitleEvent>() {
                    @Override
                    public int compare(SubtitleEvent o1, SubtitleEvent o2) {
                        if (o1.getStartTimeMilliSec() - o2.getStartTimeMilliSec() < 0)
                            return -1;
                        else if (o1.getStartTimeMilliSec() - o2.getStartTimeMilliSec() > 0)
                            return 1;
                        else
                            return 0;
                    }
                });
                onParseLoading(path, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onParseFailer(e.getMessage());
            ret = null;
        }

        return ret;
    }

    private boolean isStyleOpen(String val) {
        boolean ret = false;
        if (val != null) {
            ret = val.equals(VAL_ATTR_VS_ON);
        }
        return ret;
    }


    private String getColor(String color) {
        String ret = null;
        if (color != null && !color.equals("")) {
            if (color.length() == 8) {
                ret = "#FF" + color.substring(2);
            } else if (color.length() == 10) {
                ret = "#" + color.substring(2);
            }
        }
        return ret;
    }

    private SubtitleEvent.Alignment getAligment(String val) {
        SubtitleEvent.Alignment ret = null;
        if (val != null) {
            if (val.equals(VAL_ATTR_VS_Alignment_LeftBottom))
                ret = SubtitleEvent.Alignment.LeftBottom;
            else if (val.equals(VAL_ATTR_VS_Alignment_Bottom))
                ret = SubtitleEvent.Alignment.Bottom;
            else if (val.equals(VAL_ATTR_VS_Alignment_RightBottom))
                ret = SubtitleEvent.Alignment.RightBottom;
            else if (val.equals(VAL_ATTR_VS_Alignment_Left))
                ret = SubtitleEvent.Alignment.Left;
            else if (val.equals(VAL_ATTR_VS_Alignment_Center))
                ret = SubtitleEvent.Alignment.Center;
            else if (val.equals(VAL_ATTR_VS_Alignment_Right))
                ret = SubtitleEvent.Alignment.Right;
            else if (val.equals(VAL_ATTR_VS_Alignment_LeftTop))
                ret = SubtitleEvent.Alignment.LeftTop;
            else if (val.equals(VAL_ATTR_VS_Alignment_Top))
                ret = SubtitleEvent.Alignment.Top;
            else if (val.equals(VAL_ATTR_VS_Alignment_RightTop))
                ret = SubtitleEvent.Alignment.RightTop;
        }
        return ret;
    }

    private AssTagV4Style.Style findStyle(AssTagV4Style ats, String styleName) {
        AssTagV4Style.Style ret = null;
        if (ats != null && ats.getStyles() != null && !ats.getStyles().isEmpty()) {
            for (int i = 0; i < ats.getStyles().size(); i++) {
                if (ats.getStyles().get(i).getName().equals(styleName)) {
                    ret = ats.getStyles().get(i);
                    break;
                }
            }
        }
        return ret;
    }

    private AssTag getAssTag(String allcontent) {
        AssTag ret = null;
        AssTagScriptInfo ats = getAssTagScriptInfo(allcontent);
        AssTagV4Style atv = getAssTagV4Style(allcontent);
        AssTagEvent ate = getAssTagEvent(allcontent);
        if (ats != null && atv != null && ate != null) {
            ret = new AssTag();
            ret.setS(ats);
            ret.setV(atv);
            ret.setE(ate);
        }
        return ret;
    }

    private AssTagScriptInfo getAssTagScriptInfo(String allcontent) {
        AssTagScriptInfo ret = null;
        String partStr = getTagContent(allcontent, KEY_PART_Script_Info);
        if (partStr != null) {
            ret = new AssTagScriptInfo();
            String[] lineArr = partStr.split("\n");
            String line = null;
            for (int i = 0; i < lineArr.length; i++) {
                line = lineArr[i];
                if (line.startsWith(KEY_ATTR_SI_Title))
                    ret.setTitle(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Original_Script))
                    ret.setOriginal_Script(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Original_Translation))
                    ret.setOriginal_Translation(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Original_Timing))
                    ret.setOriginal_Timing(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Original_Editing))
                    ret.setOriginal_Editing(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Script_Updated_By))
                    ret.setScript_Updated_By(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Update_Details))
                    ret.setUpdate_Details(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_ScriptType))
                    ret.setScriptType(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Collisions))
                    ret.setCollisions(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_PlayResX))
                    ret.setPlayResX(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_PlayResY))
                    ret.setPlayResY(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Timer))
                    ret.setTimer(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_Synch_Point))
                    ret.setSynch_Point(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_WrapStyle))
                    ret.setWrapStyle(getValue(line));
                else if (line.startsWith(KEY_ATTR_SI_ScaledBorderAndShadow))
                    ret.setScaledBorderAndShadow(getValue(line));
            }
        }

        return ret;
    }

    private AssTagV4Style getAssTagV4Style(String allcontent) {
        AssTagV4Style ret = null;
        String partStr = getTagContent(allcontent, KEY_PART_v4_Styles);
        if (partStr != null) {
//            System.out.println(partStr);
            String[] lineArr = partStr.split("\n");
            String line = null;
            String[] formatNameArr = null;
            boolean hasFormat = false;
            List<AssTagV4Style.Style> styleList = null;
            for (int i = 0; i < lineArr.length; i++) {
                line = lineArr[i];
                if (line.startsWith(KEY_ATTR_VS_Format)) {
                    String formatsStr = getValue(line);
                    if (formatsStr != null && !formatsStr.equals("")) {
                        formatsStr = formatsStr.replaceAll(" ", "");
                        formatNameArr = formatsStr.split(",");
                        if (styleList == null)
                            styleList = new ArrayList<>();
                        hasFormat = true;
                    } else {
                        throwEx(ErrorNoFormat);
                    }
                }

                if (hasFormat && line.startsWith(KEY_ATTR_VS_Style)) {
                    String[] valArr = getValue(line).split(",");
                    styleList.add(getStyle(formatNameArr, valArr));
                }
            }
            if (styleList != null) {
                ret = new AssTagV4Style();
                ret.setStyles(styleList);
            }
        }
        return ret;
    }

    private AssTagEvent getAssTagEvent(String allcontent) {
        AssTagEvent ret = null;
        String partStr = getTagContent(allcontent, KEY_PART_Events);
        if (partStr != null) {
//            System.out.println(partStr);
            String[] lineArr = partStr.split("\n");
            String line = null;
            String[] formatNameArr = null;
            boolean hasFormat = false;
            List<AssTagEvent.Dialogue> dialogueList = null;
            for (int i = 0; i < lineArr.length; i++) {
                line = lineArr[i];
                if (line.startsWith(KEY_ATTR_E_Format)) {
                    String formatsStr = getValue(line);
                    if (formatsStr != null && !formatsStr.equals("")) {
                        formatsStr = formatsStr.replaceAll(" ", "");
                        formatNameArr = formatsStr.split(",");
                        if (dialogueList == null)
                            dialogueList = new ArrayList<>();
                        hasFormat = true;
                    } else {
                        throwEx(ErrorNoFormat);
                    }
                }

                if (hasFormat && line.startsWith(KEY_ATTR_E_Dialogue)) {
                    String[] valArr = getValue(line).split(",");
                    List<String> valList = new ArrayList<>();
                    boolean found = false;
                    StringBuffer sb = new StringBuffer();
//                    Log.d(TAG,"valArr:"+valArr.length+":"+Arrays.toString(valArr));
                    for (int j = 0; j < valArr.length; j++) {
                        if (valArr[j].contains("{")) {
                            found = true;
                        }
                        if (!found) {
                            valList.add(valArr[j]);
                        } else {
                            sb.append(valArr[j]);
                            if (j != valArr.length - 1) {
                                sb.append(",");
                            }
                        }
                    }
                    if(found) {
                        valList.add(sb.toString());
                    }
                    dialogueList.add(getDialogue(formatNameArr, valList));
                }
            }
            if (dialogueList != null) {
                ret = new AssTagEvent();
                ret.setDialogues(dialogueList);
            }
        }
        return ret;
    }

    private AssTagEvent.Dialogue getDialogue(String[] formatArr, List<String> valArr) {
        AssTagEvent.Dialogue ret = null;

//        System.out.println(Arrays.toString(formatArr));
//        System.out.println(valArr);
        if (formatArr.length != valArr.size()) {
            throwEx(ErrorFormatCountNotMatchValColCount + " format:" + formatArr.length + " valArr:" + valArr.size()
                    + "\n" + " formatArr:" + Arrays.toString(formatArr)
                    + "\n" + " valArr:" + valArr);
        } else {
            ret = new AssTagEvent.Dialogue();
            for (int i = 0; i < formatArr.length; i++) {
                if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Layer))
                    ret.setLayer(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Start))
                    ret.setStart(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_End))
                    ret.setEnd(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Style))
                    ret.setStyle(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Actor))
                    ret.setActor(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_MarginL))
                    ret.setMarginL(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_MarginR))
                    ret.setMarginR(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_MarginV))
                    ret.setMarginV(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Effect))
                    ret.setEffect(valArr.get(i));
                else if (formatArr[i].equals(KEY_ATTR_E_FORMAT_Text))
                    ret.setText(valArr.get(i));
            }
        }
        return ret;
    }

    private AssTagV4Style.Style getStyle(String[] formatArr, String[] valArr) {
        AssTagV4Style.Style ret = null;
        if (formatArr.length != valArr.length) {
            throwEx(ErrorFormatCountNotMatchValColCount);
        } else {
            ret = new AssTagV4Style.Style();
            for (int i = 0; i < formatArr.length; i++) {
                if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Name))
                    ret.setName(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Fontname))
                    ret.setFontname(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Fontsize))
                    ret.setFontsize(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_PrimaryColour))
                    ret.setPrimaryColour(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_SecondaryColour))
                    ret.setSecondaryColour(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_OutlineColour))
                    ret.setOutlineColour(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_BackColour))
                    ret.setBackColour(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Bold))
                    ret.setBold(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Italic))
                    ret.setItalic(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Underline))
                    ret.setUnderline(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_StrikeOut))
                    ret.setStrikeOut(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_ScaleX))
                    ret.setScaleX(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_ScaleY))
                    ret.setScaleY(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Spacing))
                    ret.setSpacing(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Angle))
                    ret.setAngle(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_BorderStyle))
                    ret.setBorderStyle(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Outline))
                    ret.setOutline(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Shadow))
                    ret.setShadow(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Alignment))
                    ret.setAlignment(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_MarginL))
                    ret.setMarginL(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_MarginR))
                    ret.setMarginR(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_MarginV))
                    ret.setMarginV(valArr[i]);
                else if (formatArr[i].equals(KEY_ATTR_VS_FORMAT_Encoding))
                    ret.setEncoding(valArr[i]);
            }
        }
        return ret;
    }

    private String getValue(String line) {
        String ret = null;
        int saperateIndex = line.indexOf(":");
        if (line != null && saperateIndex > 0 && saperateIndex != line.length() - 1) {
            ret = line.substring(line.indexOf(":") + 1);
//            String[] arr = line.split(":");
//            if (arr.length >= 2) {
//                ret = arr[1];
            if (ret.startsWith(" "))
                ret = ret.substring(1);
//            }
        }
        return ret;
    }

    private String getTagContent(String allcontent, String tag) {
        String ret = null;
        StringBuffer sb = null;
        final String tagS = "[";
        final String tagE = "]";
        String targetTag = tagS + tag + tagE;

        if (allcontent == null || !allcontent.contains(targetTag)) {
            return null;
        }

        String[] strArr = allcontent.split("\n");
        boolean start = false;
        for (int i = 0; i < strArr.length; i++) {
            if (start) {
                if (strArr[i].equals(targetTag))
                    throwEx(ErrorMultipleTag + ":" + targetTag);
                else if (strArr[i].startsWith(tagS))
                    break;
            }


            if (start) {
                if (sb == null)
                    sb = new StringBuffer();
                sb.append(strArr[i] + "\n");
            }

            //找到开头
            if (strArr[i].equals(targetTag)) {
                start = true;
            }
        }
        if (sb != null)
            ret = sb.toString();

        return ret;
    }

    public static void main(String[] args) {
        String haha = "dw,dw,,a,";
//        System.out.println(haha.split(",").length);
//        System.out.println(Arrays.toString(haha.split("dw,dw,,a,")));
        AssSubtitleParser asp = new AssSubtitleParser();

//        String part_script = testGetTagContent(KEY_PART_Script_Info);

//        AssTagScriptInfo ats = asp.getAssTagScriptInfo(getAllContent());
//        System.out.println(ats);
//        AssTagV4Style ats = asp.getAssTagV4Style(getAllContent());
//        System.out.println(ats);
        AssTagEvent ats = asp.getAssTagEvent(getAllContent());
        System.out.println(ats);

    }

    private static String getAllContent() {
        return "[Script Info]\n" +
                ";ReEdit by NetAdmin@TLF\n" +
                "\n" +
                "Title: Game.of.Thrones.S01E10.Fire.and.Blood.1080p.bluray.HEVC.AAC.5.1.x265.10Bit.mkv\n" +
                "Original Script: 衣柜军团\n" +
                "Original Translation: “龙堡字幕组”&“衣柜字幕组”\n" +
                "Original Timing: 衣柜军团\n" +
                "Original Editing: 衣柜军团\n" +
                "Script Updated By: 衣柜军团 /  NetAdmin@TLF\n" +
                "Update Details: 合成双语字幕/  剔除假药网Logo，修正格式错误和少量翻译错误\n" +
                "ScriptType: v4.00+\n" +
                "Collisions: Normal\n" +
                "PlayResX: 720\n" +
                "PlayResY: 404\n" +
                "Timer: 100.0000\n" +
                "Synch Point: \n" +
                "WrapStyle: 0\n" +
                "ScaledBorderAndShadow: no\n" +
                "\n" +
                "[V4+ Styles]\n" +
                "Format: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\n" +
                "Style: Title,Microsoft YaHei,20,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,1,1,2,10,10,10,1\n" +
                "Style: Titles_L,Microsoft YaHei,24,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,-1,0,0,0,100,100,0,0,1,1,1,1,10,10,10,1\n" +
                "Style: Author,Microsoft YaHei,16,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,1,1,2,10,10,10,1\n" +
                "Style: subtitle,Microsoft YaHei,26,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,1,1,2,10,10,10,1\n" +
                "Style: character+,Microsoft YaHei,20,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,0,0,0,0,100,100,0,0,1,1,1,4,10,10,10,1\n" +
                "Style: Character_L,Microsoft YaHei,28,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,-1,0,0,0,100,100,0,0,1,1,1,4,10,10,10,1\n" +
                "Style: character,Microsoft YaHei,20,&H00C1DFED,&H00FFFFFF,&H00000000,&H00000000,-1,0,0,0,100,100,0,0,1,1,1,2,10,10,10,1\n" +
                "\n" +
                "[Events]\n" +
                "Format: Layer, Start, End, Style, Actor, MarginL, MarginR, MarginV, Effect, Text\n" +
                "Dialogue: 0,0:00:07.80,0:00:11.07,Title,,0,0,0,,{\\fad(500,500)\\pos(360,360)\\b1\\bord0}双语特效字幕\\N{\\b0}“龙堡字幕组”携手“衣柜字幕组”诚挚奉献\\N{\\fs16}专注GOT剧集 华语冰火最专业字幕\n" +
                "Dialogue: 0,0:00:10.99,0:00:12.83,Author,,0,0,0,,{\\fad(500,500)\\pos(479,223)}翻译{\\b1\\fs24}    biscn\n" +
                "Dialogue: 0,0:00:13.20,0:00:15.00,Author,,0,0,0,,{\\fad(500,500)\\pos(185,228)}翻译{\\b1\\fs24}    Arya·Stark\n" +
                "Dialogue: 0,0:00:16.50,0:00:18.50,Title,,0,0,0,,{\\fad(500,500)\\frz82\\move(450,169,517,169)\\fs25\\b1}狭海   {\\fs12\\b0}The Narrow Sea \n" +
                "Dialogue: 0,0:00:18.60,0:00:22.20,Title,,0,0,0,,{\\fad(300,500)\\frx360\\t(20,200,\\frx0)\\pos(320,250)\\fs25\\b1}君临{\\b0\\fs12} King's Landing\n" +
                "Dialogue: 0,0:00:23.30,0:00:25.09,Author,,0,0,0,,{\\fad(500,500)\\pos(153,230)}翻译{\\b1\\fs24}    Violet_Rosa";

    }

    private static String testGetTagContent(String tag) {
        String content = getAllContent();
        AssSubtitleParser asp = new AssSubtitleParser();
        String ret = asp.getTagContent(content, tag);
        return ret;
    }

    private void throwEx(String msg) {
        throw new IllegalArgumentException(msg);
    }

}
