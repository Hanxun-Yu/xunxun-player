package org.crashxun.player.xunxun.subtitle.ass;

/**
 * Created by yuhanxun
 * 2018/9/6
 * description:
 */
public class AssTag {
    private AssTagScriptInfo s;
    private AssTagV4Style v;
    private AssTagEvent e;


    public AssTagScriptInfo getS() {
        return s;
    }

    public void setS(AssTagScriptInfo s) {
        this.s = s;
    }

    public AssTagV4Style getV() {
        return v;
    }

    public void setV(AssTagV4Style v) {
        this.v = v;
    }

    public AssTagEvent getE() {
        return e;
    }

    public void setE(AssTagEvent e) {
        this.e = e;
    }
}
