package org.crashxun.player.xunxun.subtitle.ass;
//@formatter:off

/**
 * Created by yuhanxun
 * 2018/9/5
 * description:
 * Ass format introduction:
 * reference：鳗鱼酱（来自豆瓣）
 * https://www.douban.com/note/658520175/
 * <p>
 * ①;分号开头的注释行
 * ②Format：格式行
 * ③[Script Info]部分15个行类型
 * 1、Title: 标题，是对脚本的描述，通常为作品名称或字幕组名称。
 * 2、Original Script: 原作者 ，通常为作者名称或字幕组名称。
 * 3、Original Translation: 对白的翻译者。
 * 4、Original Editing: 原校对和润色者。
 * 5、Original Timing: 时间轴制作者。
 * 6、Synch Point: 描述脚本在视频的何处开始播放，通常使用0。
 * 7、Script Updated By: 其他编辑过此原始脚本的字幕组名称。
 * 8、Update Details: 对原始脚本的更新细节——由其他字幕组制作。
 * 9、Script Type: 这是SSA脚本格式的版本信息，ASS版本为V4.00+。
 * 10、Collisions:  决定了在自动防止字幕碰撞/冲突显示时，字幕是如何移动的。
 * 包含Normal和Reverse两种模式：
 * ①若为"Normal" ，则后一条字幕将出现在前一条字幕的上方；
 * ②若为"Reverse"，则前一条字幕将向上移动给后一条字幕让位。
 * 11、PlayResX: 播放脚本时屏幕的宽度，建议与视频分辨率一致。
 * 12、PlayResY: 播放脚本时屏幕的高度，建议与视频分辨率一致。
 * ①屏幕的左上角坐标为(0,0),右下角坐标为(PlayResX数值,PlayResY数值)，所有给出的坐标(三个边距, \pos, \move, 矢量绘图等)都以此数值作为参照；
 * ② 所有的文字字号均按照此分辨率等比例放大缩小；
 * ③ 这个分辨率不影响最终显示文字的宽高比, 但影响矢量绘画图形的宽高比。
 * 13、PlayDepth: 播放脚本时颜色的深度。
 * 14、Timer: 播放速度，是百分数，100.0000%即精确的100%，小数点后有四位。
 * 默认100%。当超过100%时，将会减少字幕的整体持续时间，即意味着字幕将会越来越快地出现；低于100%时，将越来越慢。这种拉伸和压缩只在脚本播放期间起效，不会更改脚本中项目的实际时间值。
 * 15、WrapStyle: 当一个Dialogue行中存在用空格分开的多句话时，此项定义了默认换行方式：
 * ①0（默认）：智能换行，尽量平均，若无法平均，上方字幕会更长；
 * ②1：行尾换行，一行的最后一个空格才换行，只有\N可以强制换行；
 * ③2：不换行，\n和\N都可以强制换行；
 * ④3：同0，智能换行，尽量平均，若无法平均，下方的字幕会更长。
 * 16、ScaledBorderAndShadow: 边框宽度与阴影深度是否随着视频分辨率同等比例缩放。
 * ① no： 边框宽度与阴影深度完全按照指定的像素数显示；
 * ②Yes： 边框宽度与阴影深度随着实际视频的分辨率同等比例缩放.。
 * <p>
 * ④[v4+ Styles]部分的样式行
 * 1、Name                          样式名称（用于[Events]部分引用，区分大小写，不能包含逗号）
 * 2、Fontname                  字体名称（ Windows所使用的字体名称，区分大小写 ）
 * 3、Fontsize                     字体大小（字号）
 * 4、PrimaryColour          主体颜色（一般情况下文字的颜色）
 * 5、SecondaryColour     次要颜色（  在卡拉OK效果中字幕由次要颜色变为主体颜色。 ）
 * 6、OutlineColor             边框颜色
 * 7、BackColour               阴影颜色
 * 8、Bold                 粗    体（ -1=开启，0=关闭）
 * 9、Italic                 斜    体（ -1=开启，0=关闭）
 * 10 Underline         下划线 （ -1=开启，0=关闭）
 * 11 Strikeout           删除线（ -1=开启，0=关闭）
 * 12 ScaleX              横向缩放（单位 [%]，100即正常宽度）
 * 13 ScaleY              纵向缩放（单位 [%]，100即正常高度）
 * 14 Spacing            字间距（单位 [像素]，可用小数）
 * 15 Angle                旋转角度（绕z轴逆时针旋转\frz，负数=顺时针旋转。单位 [度]，可用小数）
 * 16 BorderStyle     边框样式（1=边框+阴影，3=不透明底框）
 * 17 Outline             边框宽度（单位 [像素]，可用小数）当Borderstyle 为 1 时,
 * 18 Shadow            阴影深度（单位 [像素]，可用小数，右下偏移）当Borderstyle 为 1 时,
 * 19 Alignment        对齐方式（同小键盘布局，决定了旋转/定位/缩放的参考点）
 * 20 MarginL           左边距（字幕距左边缘的距离，单位 [像素]，右对齐和中对齐时无效）
 * 21 MarginR           右边距（字幕距右边缘的距离，单位 [像素]，左对齐和中对齐时无效）
 * 22 MarginV           垂直边距（字幕距垂直边缘的距离，单位 [像素]，下对齐时表示到底部的距离、上对齐时表示到顶部的距离、中对齐时无效， 文本位于垂直中心）
 * 23 Encoding         编码（ 0=ANSI,1=默认,128=日文,134=简中,136=繁中，一般用默认1即可 ）
 * 颜色格式：&Haabbggrr，均为十六进制，取值0-F。可能为6位忽略透明度, 可能为8位,前两位透明度
 * 前2位(alpha)为透明度，00=不透明，FF=DEC255=全透明；后6是BGR蓝绿红颜色。 排在最前的00可以忽略不写, 如：{\c&HFF&}={\c&H0000FF&}为纯红色、&HFFFFFF=纯白色、&HC8000000=透明度为200的黑色。
 * <p>
 * ⑤[Events]部分的6个行类型。（行类型将在每部分分别介绍）。
 * 1 Dialogue: 这是一个“对话”事件，用于显示一些文本。
 * 2 Comment: 这是一个“评论”事件，它包含与对话、图片、声音、电影或命令事件相同的信息，但在脚本回放期间会被忽略。
 * 3 Picture: 这是一个“图片”事件 ，意味着SSA将显示指定的
 * .bmp  .jpg  .gif  .ico或.wmf图形（ 不支持.png, 且filter不支持加载图片）。
 * 4 Sound: 这是一个“声音”事件，意味着SSA将播放指定的.wav文件。 (filter不支持)
 * 5 Movie: 这是一个“影片”事件，意味着SSA将播放指定的.avi文件。 (filter不支持)
 * 6 Command: 这是一个“命令”事件，意味着SSA将执行指定的程序作为后台任务。 (filter不支持)
 * 3-6 使用频率及兼容性过低，不作讲解。
 * format:
 * 1、Layer        字幕图层（任意的整数，图层不同的两个字幕不被视为有冲突，图层号大的显示在上层）
 * 2、Start          开始时间（格式0:00:00.00 [时:分:秒:百分数]，小时只有一位数）
 * 3、End           结束时间（格式0:00:00.00 [时:分:秒:百分数]，小时只有一位数）
 * 4、Style         样式名称（引用[v4+ Styles]部分中的Name）
 * 5、Name        角色名称（用于注释此句是谁讲的，字幕中不显示，可省略，缺省后保留逗号）
 * 6、MarginL    重新设定的左边距（为四位数的像素值，0000表示使用当前样式定义的值）
 * 7、MarginR    重新设定的右边距（为四位数的像素值，0000表示使用当前样式定义的值）
 * 8、MarginV    重新设定的垂直边距（为四位数的像素值，0000表示使用当前样式定义的值）
 * 9、Effect*       过渡效果（三选一，可省略，缺省后保留逗号；效果中各参数用分号分隔）
 * ①文本/图片向上滚动：Scroll up;<y1>;<y2>;<delay>[;<fadeawayheight>]
 * ②文本/图片向下滚动：Scroll down;<y1>;<y2>;<delay>[;<fadeawayheight>]
 * y1,y2 =上下范围（不区分顺序，y1=y2=0时全屏幕滚动）；fadeawayheight=上下的淡入淡出范围；
 * delay取值1~100，它减慢了滚动的速度，0意味着没有延迟。字幕的移动速度为(1000/delay)像素/秒，即字幕从起点到终点所用时间=|y1-y2|*(delay/1000) 秒。）
 * ③文本/图片横向移动：Banner;<delay>[;<lefttoright>][;<fadeawaywidth>]
 * lefttoright=0/1：0=从右向左移动(默认), 1=从左向右移动, 可省略；
 * fadeawaywidth=左右的淡入淡出范围
 * deprecated -> Karaoke：卡拉OK效果，在ASS中该Effect已经废弃不用，变为在Text中使用代码实现。
 * 10、Text         Dialogue:字幕文本( 这是真正作为字幕在银幕上显示的文本。第9个逗号之后的所有内容都被视为字幕文本，因此它可以包含逗号。它还可以通过以下代码来改变[v4+ Styles]部分中的字体属性，并实现各种特效)
 * Picture、Sound、Movie、Command的Text为文件的完整路径和名称。
 * <p>
 * 附录： 样式覆盖代码
 * 所有代码均由反斜杠 \ 开头
 * 除\n，\h，\N外，所有代码均输入在 { } 内，一个 { } 内可存在多个代码
 * 下列代码中的说明符号(实际使用中没有)含义：<参数>，<../..>选择一项，[可缺省项]
 * <p>
 * ①基础代码
 * 1、空格与换行
 * \n 空格(若宽度超出范围则空格后自动换行)，
 * \h 硬空格(不换行)，\N 硬回车(两行之间没有空隙)
 * <p>
 * \q<0,1,2,3> 更改 脚本中的WrapStyle换行方式：
 * ①0（默认）：智能换行，尽量平均，若无法平均，上方字幕会更长；
 * ②1：行尾换行，一行的最后一个空格才换行，只有\N可以强制换行；
 * ③2：不换行，\n和\N都可以强制换行；
 * ④3：同0，智能换行，尽量平均，若无法平均，下方的字幕会更长。
 * <p>
 * 2、改变字体效果
 * \fn<font name> 改变字体（如：\fn微软雅黑）
 * \fs<font size>    改变字体大小（不可用小数）
 * \r [<style>]  改变成其它的字体样式，缺省则表示恢复成原来的字体样式，如：{\fn微软雅黑}你{\r}好
 * \b <0/1>  粗体，\i <0/1>斜体,
 * 这里多打了一个反斜杠\\u <0/1> 下划线，\s <0/1>  删除线（0=关闭，1=开启）
 * \fa<x,y><degrees>倾斜度（可用小数，负数=反向， \fax-0.5等同于斜体，一般不要超过±2）
 * \bord[<x,y>]<width>  边框宽度（可用小数，0=隐藏；可分别设置x,y上的宽度）
 * \shad[<x,y>]<depth> 阴影深度（可用小数，0=隐藏；可分别设置x,y上的偏移量；可负，右下为正）
 * <p>
 * \fsc<x/y><percent> 字符缩放（可用小数，不可负，不带百分号）
 * \fsp<pixels> 调整字间距（可用小数，可负，默认0）
 * \fr[<x/y/z>]<degrees> 旋转（可用小数，可大于360，负数则反向旋转；\fr = \frz；旋转中心由对齐方式决定，可通过\org(x,y)重设旋转中心）
 * <p>
 * \c&H<bbggrr>&     改变主体颜色（同\1c）
 * \1c&H<bbggrr>&   改变主体颜色
 * \2c&H<bbggrr>&   改变次要颜色
 * \3c&H<bbggrr>&   改变边框颜色
 * \4c&H<bbggrr>&   改变阴影颜色
 * \alpha&H<aa>&    改变字幕所有部分的透明度
 * \1a&H<aa>&         改变主体透明度
 * \2a&H<aa>&         改变次要透明度
 * \3a&H<aa>&         改变边框透明度
 * \4a&H<aa>&         改变阴影透明度
 * <p>
 * \fe<charset> 改变编码
 * <p>
 * 3、改变字幕位置
 * \an<alignment> 设置对齐方式
 * ①对齐方式同小键盘，如：\an1左下对齐，\an5中对齐，\an9右上对齐
 * ②采用何种对齐方式，字幕的旋转/缩放/移动时的参照点，就位于字幕虚拟底框的何处，如：\an5时，参照点就在字幕中心；\an2时，参照点在字幕底边中点。
 * ③一条字幕里只能用一次
 * <p>
 * \pos(<x>, <y>) 字幕定位
 * ①x为横坐标，y为纵坐标，可用小数；参照点(定位点)由对齐方式决定
 * ②使用\move或\pos后MarginL,MarginR,MarginV无效
 * ③定位技巧：PlayResX和 PlayResY设为电脑屏幕尺寸，使用QQ或微信截图工具获取POS位置
 * ④一条字幕里只能用一次，不能和\move同时使用
 * <p>
 * 4、边缘模糊
 * \be<整数>   普通模糊     \blur<可小数> 高斯模糊
 * 可用小数，当无边框时，模糊字体本身；\blur可以制作边框荧光效果
 * <p>
 * 5、蒙板
 * \clip(x1,y1,x2,y2) 蒙版裁剪（矩形）
 * ①x1,y1为左上角坐标；x2,y2为右下角坐标
 * ②只有此矩形内的字幕可以显示
 * <p>
 * \iclip([scale,]<drawing commands>) 蒙版裁剪（图形）
 * level 等级越大，图形越小，且蒙板位置会有偏移，缺省后按照实际绘图代码大小进行裁剪
 * <p>
 * \iclip  排除蒙板 使矩形/绘制图形外的字幕才可以显示
 * <p>
 * 6、绘图命令（可直接使用Aegisub附带的ASSDraw软件画图，自动生成绘图命令）
 * \p<1/2...><Drawing commands> \p0
 * ①参数1/2...表示绘图比例， 为坐标的缩放等级, 按2的(等级-1)次方计算。如\clip4：2^(4-1)=8, 即将后面的坐标缩至1/8。\p0表示绘图结束
 * ②绘制出的图形作为一个字符来处理，可以进行旋转、缩放等操作
 * ③绘制的图形必须是封闭的，否则将自动添加直线使之封闭。绘制的图形是实心的，在前面添加\1a&Hff&可以得到空心的图形
 * ④Drawing commands(绘图命令):
 * m <x> <y> 移动指针到(x,y)，同时将现有的图形封闭(即开始画新的图形), 所有绘图都以这个命令开始；
 * n <x> <y> 移动指针到(x,y)，但不封闭上一个图形；
 * l <x> <y> 从指针位置画直线到(x,y)（可用 l<x1> <y2> <x2> <y2> ... <xn> <yn>的方式画连续直线）；
 * b <x1> <y1> <x2> <y2> <x3> <y3> 画一条三度贝塞尔曲线至(x3, y3), 以(x1, y1), (x2, y2)作为控制点 ；
 * s <x1> <y1> <x2> <y2> <x3> <y3> ... <xn> <yn> 画曲线至点(xn,yn)，n≥3，中间均为控制点；
 * p <x> <y> 延伸b样条到点(x,y)， 作用相当于在s命令后多加一个坐标点(x, y) ；
 * c 结束b样条；
 * 例：枫叶的绘图命令：
 * m 0 0 b -37 1 -72 -11 -106 -22 b -70 -27 -38 -56 1 -44 b -23 -62 -45 -94 -62 -133 b -11 -104 28 -87 42 -44 b 38 -81 38 -117 51 -158 b 70 -129 74 -95 73 -65 b 82 -82 98 -85 106 -107 b 110 -77 101 -39 83 -29 b 100 -32 114 -22 134 -30 b 112 -14 82 -14 82 -14 b 65 20 71 54 75 87 b 74 89 72 89 71 87 b 69 54 63 23 52 -10 b 58 21 48 28 43 57 b 40 43 38 22 34 14 b 14 45 -13 38 -37 63 b -21 42 -19 21 0 0
 * <p>
 * \pbo<y> 定义所绘图形的基线偏移值(baseline offset)
 * 当y>0时, 图形的所有坐标沿y轴向下移指定的像素值
 * 当y<0时, 图形的所有坐标沿y轴向上移指定的像素值
 * <p>
 * ②动画代码
 * 1、卡拉OK
 * \k<duration>
 * ①时间单位 [1/100秒]，\k50 =0.5x100 =用0.5秒来显示歌词卡拉OK效果
 * ②效果前显示次要颜色，效果后显示主体颜色
 * ③无平滑效果， 按照每一分隔好的小段来进行高亮显示 ，如：{\k50}真的{\k50}爱{\k50}你（“真的”同时变色）
 * <p>
 * \K或\kf  平滑卡拉OK效果，从左到右流畅填充。{\K80}你好{K20}吗（变速）
 * <p>
 * \ko 字体边框的卡拉OK效果，效果前无边框，效果后显示边框
 * <p>
 * \kt 控制卡拉OK显示时间，控制该音节从整行开始过了多长时间后开始填充，如：{\K10}真{\kt20\K10}的{\kt10\K10}爱{K10}你，开始时之间填充“真”，到达100ms开始填充“爱”，到达200ms开始填充“的”，此时“爱”填充完毕，所以“你”开始填充
 * <p>
 * 2、匀速移动
 * \move(<x1>, <y1>, <x2>, <y2>[, <t1>, <t2>])
 * ①x1,y1为移动开始的位置，x2,y2为移动结束的位置；参照点(移动点)由对齐方式决定
 * ②t1,t2是移动开始和结束时间，单位 [ms]，缺省则在此字幕持续时间内进行移动
 * ③所有变量均可用小数
 * ④一条字幕里只能用一次，不能和\pos同时使用
 * <p>
 * 3、淡出淡入
 * \fad(<t1>, <t2> ) 简易淡入淡出
 * t1表示淡入使用的时间；t2表示淡出使用的时间
 * <p>
 * \fade(<a1>, <a2>, <a3>, <t1>, <t2>, <t3>, <t4>) 复杂淡入淡出
 * ①a1是淡入开始时的透明度，t1,t2表示淡入的开始时间和结束时间
 * ②a2是淡入结束到淡出开始时的透明度
 * ③a3是淡出结束时的透明度，t3,t4表示淡出的开始时间和结束时间
 * ④此处透明度必须使用十进制来表示，范围0-255
 * <p>
 * (所有t均包含在字幕持续时间内，因此所有t之和不得超过字幕持续时间)
 * <p>
 * 4、动态效果 \t([<t1>, <t2>, ] [<accel>,] <style modifiers> )  ==  \t(开始时间,结束时间,加速度,特效码)
 * ①t1，t2分别是动态效果开始时间和结束时间，单位 [ms]，缺省则在此字幕持续时间内进行动态效果
 * ②accel为加速度（accel=0 无效果；0<accel<1 减速；accel=1 匀速；1<accel 加速，缺省则为1 ）
 * 计算公式： t 时的动作完成量 = pow( (t-t1)/(t2-t1), accel )
 * ③不能使用的style modifiers：\b，\i，这里多打了一个反斜杠\\u，\s，\org，\fn，\fe，\an，\r，\q，\pos，\k，\ko，\kf，\K，\kt，\move，\fad，\fade，\p，\pbo
 * ④除了③中的代码均可使用，且可同时使用多个特效，可用代码例举：\c，\<1/2/3/4>c，\alpha，\<1/2/3/4>a，\fs，\fr[<x/y/z>]，\fsc<x/y>，\fsp，\bord，\shad，\clip，\iclip（\clip和\iclip只能用矩形蒙板，不可用图形蒙板）
 * ⑤代码举例：
 * {\fs10\t(0,500,\fs50\frz30))}：字幕在0~0.5s从10号放大到50号，并逆时针旋转30度。
 * <p>
 * {\c&H00FF8000&\t(0,500,\c&H0080ff00)} 字幕在0~0.5s从蓝色过渡到绿色（嵌套可以实现霓虹灯效果）。
 * <p>
 * {\fs100\t(1000,2000,\fs10\t(2000,3000,\fs50))} 1~2s字体从100缩小到10，2~3s再放大到50。
 * <p>
 * {\frz360\t(51,100,\frz390\t(100,150,\frz360\t(150,200,\frz330\t(200,250,\frz360\t(250,300,\frz390\t(200,250,\frz360\t(300,350,\frz330\t(350,400,\frz360)))))))))} 抖动。
 * <p>
 * {\t(0,700,\alpha&HFF\t(700,1400,\alpha&H00\t(1400,2100,\alpha&FF)))}闪烁。
 * <p>
 * {\iclip(x1,y1,x2,y2)\t(0,500,\iclip(x2,y1,x2,y2))} 字幕在0~0.5s从左到右显示。
 */
//@formatter:on
public interface AssSubtitleDefine {

    String KEY_PART_Script_Info = "Script Info";
    String KEY_PART_v4_Styles = "V4+ Styles";
    String KEY_PART_Events = "Events";
    //[Fonts]、[Graphics] not support

    //[Script Info]
    //[KEY]
    String KEY_ATTR_SI_Title = "Title";
    String KEY_ATTR_SI_Original_Script = "Original Script";
    String KEY_ATTR_SI_Original_Editing = "Original Editing";
    String KEY_ATTR_SI_Original_Timing = "Original Timing";
    String KEY_ATTR_SI_Original_Translation = "Original Translation";
    String KEY_ATTR_SI_Script_Updated_By = "Script Updated By";
    String KEY_ATTR_SI_Update_Details = "Update Details";
    String KEY_ATTR_SI_ScriptType = "ScriptType";
    String KEY_ATTR_SI_Collisions = "Collisions";
    String KEY_ATTR_SI_PlayResX = "PlayResX";
    String KEY_ATTR_SI_PlayResY = "PlayResY";
    String KEY_ATTR_SI_Timer = "Timer";
    String KEY_ATTR_SI_Synch_Point = "Synch Point";
    String KEY_ATTR_SI_WrapStyle = "WrapStyle";
    String KEY_ATTR_SI_ScaledBorderAndShadow = "ScaledBorderAndShadow";
    //[VAL]
    String VAL_ATTR_SI_Collisions_Normal = "Normal";
    String VAL_ATTR_SI_Collisions_Reverse = "Reverse";
    String VAL_ATTR_SI_WrapStyle_intelligent_up = "0";
    String VAL_ATTR_SI_WrapStyle_lineEnd = "1";
    String VAL_ATTR_SI_WrapStyle_none = "2";
    String VAL_ATTR_SI_WrapStyle_intelligent_down = "3";
    String VAL_ATTR_SI_ScaledBorderAndShadow_yes = "yes";
    String VAL_ATTR_SI_ScaledBorderAndShadow_no = "no";

    //[V4+ Styles]
    //[KEY]
    String KEY_ATTR_VS_Format = "Format";
    String KEY_ATTR_VS_Style = "Style";
    String KEY_ATTR_VS_FORMAT_Name = "Name";
    String KEY_ATTR_VS_FORMAT_Fontname = "Fontname";
    String KEY_ATTR_VS_FORMAT_Fontsize = "Fontsize";
    String KEY_ATTR_VS_FORMAT_PrimaryColour = "PrimaryColour";
    String KEY_ATTR_VS_FORMAT_SecondaryColour = "SecondaryColour";
    String KEY_ATTR_VS_FORMAT_OutlineColour = "OutlineColour";
    String KEY_ATTR_VS_FORMAT_BackColour = "BackColour";
    String KEY_ATTR_VS_FORMAT_Bold = "Bold";
    String KEY_ATTR_VS_FORMAT_Italic = "Italic";
    String KEY_ATTR_VS_FORMAT_Underline = "Underline";
    String KEY_ATTR_VS_FORMAT_StrikeOut = "StrikeOut";
    String KEY_ATTR_VS_FORMAT_ScaleX = "ScaleX";
    String KEY_ATTR_VS_FORMAT_ScaleY = "ScaleY";
    String KEY_ATTR_VS_FORMAT_Spacing = "Spacing";
    String KEY_ATTR_VS_FORMAT_Angle = "Angle";
    String KEY_ATTR_VS_FORMAT_BorderStyle = "BorderStyle";
    String KEY_ATTR_VS_FORMAT_Outline = "Outline";
    String KEY_ATTR_VS_FORMAT_Shadow = "Shadow";
    String KEY_ATTR_VS_FORMAT_Alignment = "Alignment";
    String KEY_ATTR_VS_FORMAT_MarginL = "MarginL";
    String KEY_ATTR_VS_FORMAT_MarginR = "MarginR";
    String KEY_ATTR_VS_FORMAT_MarginV = "MarginV";
    String KEY_ATTR_VS_FORMAT_Encoding = "Encoding";
    //[VAL]
    String VAL_ATTR_VS_ON = "-1";
    String VAL_ATTR_VS_OFF = "0";
    String VAL_ATTR_VS_BorderStyle_shadowBord = "1";
    String VAL_ATTR_VS_BorderStyle_unTranspBottomBord = "3";
    String VAL_ATTR_VS_Alignment_LeftBottom = "1";
    String VAL_ATTR_VS_Alignment_Bottom = "2";
    String VAL_ATTR_VS_Alignment_RightBottom = "3";
    String VAL_ATTR_VS_Alignment_Left = "4";
    String VAL_ATTR_VS_Alignment_Center = "5";
    String VAL_ATTR_VS_Alignment_Right = "6";
    String VAL_ATTR_VS_Alignment_LeftTop = "7";
    String VAL_ATTR_VS_Alignment_Top = "8";
    String VAL_ATTR_VS_Alignment_RightTop = "9";
    String VAL_ATTR_VS_Encoding_ANSI = "0";
    String VAL_ATTR_VS_Encoding_DEFAULT = "1";
    String VAL_ATTR_VS_Encoding_JAPANESE = "128";
    String VAL_ATTR_VS_Encoding_SIMPLE_CHINESE = "134";
    String VAL_ATTR_VS_Encoding_TRADITIONAL_CHINESE = "136";


    //[Events]
    //[KEY]
    String KEY_ATTR_E_Format = "Format";
    String KEY_ATTR_E_Dialogue = "Dialogue";
    String KEY_ATTR_E_FORMAT_Layer = "Layer";
    String KEY_ATTR_E_FORMAT_Start = "Start";
    String KEY_ATTR_E_FORMAT_End = "End";
    String KEY_ATTR_E_FORMAT_Style = "Style";
    String KEY_ATTR_E_FORMAT_Actor = "Actor";
    String KEY_ATTR_E_FORMAT_MarginL = "MarginL";
    String KEY_ATTR_E_FORMAT_MarginR = "MarginR";
    String KEY_ATTR_E_FORMAT_MarginV = "MarginV";
    String KEY_ATTR_E_FORMAT_Effect = "Effect";
    String KEY_ATTR_E_FORMAT_Text = "Text";
    //[VAL]
    String VAL_ATTR_E_Effect_Scroll_up = "Scroll up";
    String VAL_ATTR_E_Effect_Scroll_down = "Scroll down";
    String VAL_ATTR_E_Effect_Banner = "Banner";
    String VAL_ATTR_E_Effect_Banner_right2left = "0";
    String VAL_ATTR_E_Effect_Banner_left2right = "1";

    //{OVER_STYLE} 除\n，\h，\N外，所有代码均输入在 { } 内，一个 { } 内可存在多个代码
    String OS_On = "1";
    String OS_Off = "0";
    String OS_Space_autowrap = "\\n";
    String OS_Space_nowrap = "\\h";
    String OS_Wrap = "\\N";
    String OS_WrapStyle = "\\q"; //val is VAL_ATTR_SI_WrapStyle_XXX
    String OS_FontName = "\\fn";
    String OS_FontSize = "\\fs";
    String OS_StyleChange = "\\r";
    String OS_Text_Bold = "\\b";
    String OS_Text_Italic = "\\i";
    String OS_Text_Underline = "\\u";
    String OS_Text_Deleteline = "\\s";
    String OS_Text_Gradient = "\\fa";// \fa<x,y><degrees> \fax-0.5等同于斜体
    String OS_BordWidth = "\\bord";
    String OS_ShadowDepth = "\\shad";
    String OS_Color_Primary = "\\c";
    String OS_Color_Primary2 = "\\1c";
    String OS_Color_Secondary = "\\2c";
    String OS_Color_Bord = "\\3c";
    String OS_Color_Shadow = "\\4c";
    String OS_Alpha_All = "\\alpha";
    String OS_Alpha_Primary = "\\1a";
    String OS_Alpha_Secondary = "\\2a";
    String OS_Alpha_Bord = "\\3a";
    String OS_Alpha_Shadow = "\\4a";
    String OS_Encoding = "\\fe";
    String OS_Align_LeftBottom = "\\an1";
    String OS_Align_Center = "\\an5";
    String OS_Align_RightUp = "\\an9";
    String OS_Fuzzy = "\\be";
    String OS_Masking = "\\iclip";
    String OS_Draw = "\\p";
    String OS_Draw_BaselineOffset = "\\pbo";
    String OS_Anim_Karaoke = "\\k";
    String OS_Anim_KaraokeSmooth = "\\K";
    String OS_Anim_KaraokeSmooth2 = "\\kf";
    String OS_Anim_KaraokeShowTime = "\\kt";
    String OS_Anim_Move = "\\move";
    String OS_Anim_FadeSimple = "\\fad";
    String OS_Anim_Fade = "\\fade";
    String OS_Anim_DynamicOS = "\\t";

}
