package org.crashxun.player.xunxun.subtitle.ass;

/**
 * Created by yuhanxun
 * 2018/9/6
 * description:
 */
public class AssTagScriptInfo {
    private String Title;
    private String Original_Script;
    private String Original_Translation;
    private String Original_Timing;
    private String Original_Editing;
    private String Script_Updated_By;
    private String Update_Details;
    private String ScriptType;
    private String Collisions;
    private String PlayResX;
    private String PlayResY;
    private String Timer;
    private String Synch_Point;
    private String WrapStyle;
    private String ScaledBorderAndShadow;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getOriginal_Script() {
        return Original_Script;
    }

    public void setOriginal_Script(String original_Script) {
        Original_Script = original_Script;
    }

    public String getOriginal_Translation() {
        return Original_Translation;
    }

    public void setOriginal_Translation(String original_Translation) {
        Original_Translation = original_Translation;
    }

    public String getOriginal_Timing() {
        return Original_Timing;
    }

    public void setOriginal_Timing(String original_Timing) {
        Original_Timing = original_Timing;
    }

    public String getOriginal_Editing() {
        return Original_Editing;
    }

    public void setOriginal_Editing(String original_Editing) {
        Original_Editing = original_Editing;
    }

    public String getScript_Updated_By() {
        return Script_Updated_By;
    }

    public void setScript_Updated_By(String script_Updated_By) {
        Script_Updated_By = script_Updated_By;
    }

    public String getUpdate_Details() {
        return Update_Details;
    }

    public void setUpdate_Details(String update_Details) {
        Update_Details = update_Details;
    }

    public String getScriptType() {
        return ScriptType;
    }

    public void setScriptType(String scriptType) {
        ScriptType = scriptType;
    }

    public String getCollisions() {
        return Collisions;
    }

    public void setCollisions(String collisions) {
        Collisions = collisions;
    }

    public String getPlayResX() {
        return PlayResX;
    }

    public void setPlayResX(String playResX) {
        PlayResX = playResX;
    }

    public String getPlayResY() {
        return PlayResY;
    }

    public void setPlayResY(String playResY) {
        PlayResY = playResY;
    }

    public String getTimer() {
        return Timer;
    }

    public void setTimer(String timer) {
        Timer = timer;
    }

    public String getSynch_Point() {
        return Synch_Point;
    }

    public void setSynch_Point(String synch_Point) {
        Synch_Point = synch_Point;
    }

    public String getWrapStyle() {
        return WrapStyle;
    }

    public void setWrapStyle(String wrapStyle) {
        WrapStyle = wrapStyle;
    }

    public String getScaledBorderAndShadow() {
        return ScaledBorderAndShadow;
    }

    public void setScaledBorderAndShadow(String scaledBorderAndShadow) {
        ScaledBorderAndShadow = scaledBorderAndShadow;
    }

    @Override
    public String toString() {
        return "AssTagScriptInfo{" +
                "Title='" + Title + '\'' +
                ", Original_Script='" + Original_Script + '\'' +
                ", Original_Translation='" + Original_Translation + '\'' +
                ", Original_Timing='" + Original_Timing + '\'' +
                ", Original_Editing='" + Original_Editing + '\'' +
                ", Script_Updated_By='" + Script_Updated_By + '\'' +
                ", Update_Details='" + Update_Details + '\'' +
                ", ScriptType='" + ScriptType + '\'' +
                ", Collisions='" + Collisions + '\'' +
                ", PlayResX='" + PlayResX + '\'' +
                ", PlayResY='" + PlayResY + '\'' +
                ", Timer='" + Timer + '\'' +
                ", Synch_Point='" + Synch_Point + '\'' +
                ", WrapStyle='" + WrapStyle + '\'' +
                ", ScaledBorderAndShadow='" + ScaledBorderAndShadow + '\'' +
                '}';
    }
}
