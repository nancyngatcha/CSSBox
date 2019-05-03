package org.fit.cssbox.layout;

import cz.vutbr.web.css.*;
import org.w3c.dom.Element;

import java.awt.*;

public class FlexItemBlockBox extends BlockBox  implements Comparable<FlexItemBlockBox>  {

    public static final CSSProperty.AlignSelf ALIGN_SELF_AUTO = CSSProperty.AlignSelf.AUTO;
    public static final CSSProperty.AlignSelf ALIGN_SELF_FLEX_START = CSSProperty.AlignSelf.FLEX_START;
    public static final CSSProperty.AlignSelf ALIGN_SELF_FLEX_END = CSSProperty.AlignSelf.FLEX_END;
    public static final CSSProperty.AlignSelf ALIGN_SELF_CENTER = CSSProperty.AlignSelf.CENTER;
    public static final CSSProperty.AlignSelf ALIGN_SELF_BASELINE = CSSProperty.AlignSelf.BASELINE;
    public static final CSSProperty.AlignSelf ALIGN_SELF_STRETCH = CSSProperty.AlignSelf.STRETCH;

    public static final CSSProperty.FlexBasis FLEX_BASIS_CONTENT = CSSProperty.FlexBasis.CONTENT;
    public static final CSSProperty.FlexBasis FLEX_BASIS_LENGTH = CSSProperty.FlexBasis.length;
    public static final CSSProperty.FlexBasis FLEX_BASIS_PERCENTAGE = CSSProperty.FlexBasis.percentage;
    public static final CSSProperty.FlexBasis FLEX_BASIS_AUTO = CSSProperty.FlexBasis.AUTO;

    public static final CSSProperty.FlexGrow FLEX_GROW_NUMBER = CSSProperty.FlexGrow.number;

    public static final CSSProperty.FlexShrink FLEX_SHRINK_NUMBER = CSSProperty.FlexShrink.number;


    /** flex basis specified by the style */
                                                //INITIALS
    protected CSSProperty.AlignSelf alignSelf; //auto
    protected CSSProperty.FlexBasis flexBasis; // auto
    protected CSSProperty.FlexGrow flexGrow; // 0
    protected CSSProperty.FlexShrink flexShrink; // 1
    protected CSSProperty.Order flexOrder; //0

    protected float flexGrowValue;
    protected float flexShrinkValue;
    protected int flexOrderValue;

    protected boolean flexBasisSetByCont;

    protected int hypotheticalMainSize;

    protected boolean crossSizeSetByCont;
    protected boolean crossSizeSetByPercentage;

    public FlexItemBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        isblock = true;
        hypotheticalMainSize = 0;
        flexBasisSetByCont = false;
        crossSizeSetByCont = false;
        crossSizeSetByPercentage = false;
    }

    public FlexItemBlockBox(InlineBox src) {
        super(src);
        isblock = true;
        hypotheticalMainSize = 0;
        flexBasisSetByCont = false;
        crossSizeSetByCont = false;
        crossSizeSetByPercentage = false;
    }


    public int getFlexOrderValue() {
        return flexOrderValue;
    }

    @Override
    public void setStyle(NodeData s)
    {
        super.setStyle(s);
        loadFlexItemsStyles();

    }

    public void loadFlexItemsStyles(){

        alignSelf = style.getProperty("align-self");
        if (alignSelf == null) alignSelf = CSSProperty.AlignSelf.AUTO;

        flexBasis = style.getProperty("flex-basis");
        if (flexBasis == null) flexBasis = CSSProperty.FlexBasis.AUTO;

        flexGrow = style.getProperty("flex-grow");
        if (flexGrow == null) flexGrow = CSSProperty.FlexGrow.INITIAL;

        flexShrink = style.getProperty("flex-shrink");
        if (flexShrink == null) flexShrink = CSSProperty.FlexShrink.INITIAL;

        flexOrder = style.getProperty("order");
        if (flexOrder == null) flexOrder = CSSProperty.Order.INITIAL;

        setFactorValues();
        setFlexOrder();
    }

    //ZDE SE RESI NUMBER
    private void setFactorValues() {
        Term<?> len = style.getValue("flex-grow", false);
        flexGrowValue = setFactor(len, true);
        len = style.getValue("flex-shrink", false);
        flexShrinkValue = setFactor(len, false);
    }

    private float setFactor(Term<?> len, boolean isGrow){
        float ret;
        if(len != null) {
            if (len instanceof TermInteger) ret = ((TermInteger) len).getValue();
            else ret = ((TermNumber) len).getValue();
        } else {
            if(isGrow) ret = 0;
            else ret = 1;
        }
        return ret;
    }

    private void setFlexOrder(){
        TermInteger len = (TermInteger) style.getValue("order", false);

        if(len != null)
            flexOrderValue = len.getValue().intValue();
        else
            flexOrderValue = 0;
    }

    protected void disableFloats(){
        floatY = 0;
        floatXl = 0;
        floatXr = 0;
        floating = CSSProperty.Float.NONE;
    }

    protected int setHypotheticalMainSize(FlexContainerBlockBox container){
        CSSDecoder dec = new CSSDecoder(container.ctx);
        int contw = container.getContentWidth();

        if(flexBasis == FLEX_BASIS_LENGTH || flexBasis == FLEX_BASIS_PERCENTAGE) {
            //used flex-basis
            if(flexBasis == FLEX_BASIS_PERCENTAGE && !container.isRowContainer()) {
                if(container.content.height != 0){
                    hypotheticalMainSize = dec.getLength(getLengthValue("flex-basis"), false, 0, 0, container.mainSize);

                } else {
                    if (style.getProperty("height") == CSSProperty.Height.AUTO || style.getProperty("height") == null || style.getProperty("height") == CSSProperty.Height.valueOf("percentage")) {
                        flexBasisSetByCont = true;
                    } else {
                        hypotheticalMainSize = dec.getLength(getLengthValue("height"), false, 0, 0, 0); //use height
                        flexBasisSetByCont = false;
                    }
                }
            } else {
                hypotheticalMainSize = dec.getLength(getLengthValue("flex-basis"), false, 0, 0, contw);
            }

            //when used flex basis is smaller than min content -> flex basis = min content)
            if(container.isRowContainer()) {
                if (hypotheticalMainSize < getMinimalContentWidth())
                    hypotheticalMainSize = getMinimalContentWidth();

            }
        }
        else if (flexBasis == FlexItemBlockBox.FLEX_BASIS_CONTENT){
            flexBasisSetByCont = true;
        }
        else if (flexBasis == FlexItemBlockBox.FLEX_BASIS_AUTO){
            if(container.isRowContainer()) {
                //ROW
                if (style.getProperty("width") == CSSProperty.Width.AUTO || style.getProperty("width") == null) {
                    flexBasisSetByCont = true;
                }
                else {
                    hypotheticalMainSize = dec.getLength(getLengthValue("width"), false, 0, 0, contw); //use width
                }


            } else {
                //COLUMN
                if (style.getProperty("height") == CSSProperty.Height.AUTO  || style.getProperty("height") == null) {
                    flexBasisSetByCont = true;
                } else if(style.getProperty("height") == CSSProperty.Height.valueOf("percentage")){
                    if(container.content.height != 0) {
                        hypotheticalMainSize = dec.getLength(getLengthValue("height"), false, 0, 0, container.mainSize);
                    } else {
                        flexBasisSetByCont = true;
                    }
                } else {
                    hypotheticalMainSize = dec.getLength(getLengthValue("height"), false, 0, 0, 0); //use height
                    flexBasisSetByCont = false;
                }
            }
        }


        if(flexBasisSetByCont && container.isRowContainer())
            setFlexBasisBasedByContent();

        return hypotheticalMainSize;
    }

    protected int boundFlexBasisByMinAndMax(int value) {
        if(((FlexContainerBlockBox) parent).isRowContainer()) {
            if (min_size.width != -1)
                if (value < min_size.width)
                    value = min_size.width;

            if (max_size.width != -1)
                if (value > max_size.width)
                    value = max_size.width;

        } else {
            if (min_size.height != -1)
                if (value < min_size.height)
                    value = min_size.height;

            if (max_size.height != -1)
                if (value > max_size.height)
                    value = max_size.height;
        }
        return value;

    }

    private void setFlexBasisBasedByContent(){
        System.out.println("FLEX BASIS JE SETNUTA CONTENTEM");
        hypotheticalMainSize = getMaximalContentWidth();
    }


    protected void fixRightMargin(){
        FlexContainerBlockBox parent = (FlexContainerBlockBox) this.getContainingBlockBox();
        CSSDecoder decoder = new CSSDecoder(parent.ctx);

        if(style.getProperty("margin-right") != CSSProperty.Margin.AUTO || style.getProperty("margin") != CSSProperty.Margin.AUTO) {
            margin.right = decoder.getLength(getLengthValue("margin-right"), false, 0, 0, 0);
            emargin.right = margin.right;
        }

    }

    protected boolean isNotAlignSelfAuto(){
        if(alignSelf == CSSProperty.AlignSelf.AUTO)
            return false;
        else
            return true;
    }

    @Override
    public int compareTo(FlexItemBlockBox o) {
        return this.flexOrderValue - o.getFlexOrderValue();
    }

    protected void setCrossSize(FlexContainerBlockBox container){
        CSSDecoder dec = new CSSDecoder(ctx);
        if(container.isRowContainer()) {
            if (style.getProperty("height") == null && style.getProperty("min-height") == null) {
                //vyska itemu u row direction nenastavena
                crossSizeSetByCont = true;
            } else if ((style.getProperty("height") == CSSProperty.Height.percentage) && !getContainingBlockBox().hasFixedHeight()) {
                //vyska itemu u row direction nastavena v %
                crossSizeSetByPercentage = true;
            } else if (style.getProperty("height") == CSSProperty.Height.length) {
                setContentHeight(dec.getLength(getLengthValue("height"), false, 0, 0, 0));
            }
        } else {
            int contw = container.crossSize;
            int width = dec.getLength(getLengthValue("width"), false, -1, -1, contw);

            if(width != -1){
                content.width = width;
                if(content.width < min_size.width && min_size.width != -1)
                    content.width = min_size.width;

                if(content.width > max_size.width && max_size.width != -1)
                    content.width = max_size.width;
            } else {
                content.width = getMaximalContentWidth();

                if(content.width + margin.left + margin.right + padding.left + padding.right + border.right + border.left> container.crossSize) {
                    content.width = container.crossSize - margin.left - margin.right - padding.left - padding.right - border.right - border.left;
                    if(getMinimalContentWidth()  + margin.left + margin.right + padding.left + padding.right + border.right + border.left > container.crossSize)
                        content.width = getMinimalContentWidth();
                }

                crossSizeSetByCont = true;
            }

        }
    }

    protected void adjustHeight(FlexContainerBlockBox container){
        if(getSubBoxNumber() != 0){
            if(flexBasis != FlexItemBlockBox.FLEX_BASIS_AUTO && flexBasis != FlexItemBlockBox.FLEX_BASIS_CONTENT){
                int contheight = getSubBox(getSubBoxNumber()-1).bounds.y + getSubBox(getSubBoxNumber()-1).bounds.height;
                if(contheight > content.height) {
                    content.height = contheight;
                    content.height = boundByHeight(container);
                }

            } else if(flexBasisSetByCont){
                content.height = getSubBox(getSubBoxNumber()-1).getContentY() + getSubBox(getSubBoxNumber()-1).getContentHeight();

            }
        } else {
            content.height = 0;
        }
    }

    protected int boundByHeight(FlexContainerBlockBox container){
        CSSDecoder dec = new CSSDecoder(ctx);
        int height = dec.getLength(getLengthValue("height"), false, -1, -1, container.mainSize);
        if(height != -1){
            //height nenastaveno
            if(style.getProperty("height") == CSSProperty.Height.percentage && container.mainSize == 0)
                return content.height;

            if(height < content.height) {
                if(height > hypotheticalMainSize)
                    content.height = height;
                else
                    content.height = hypotheticalMainSize;
            }
        } else {
            int minheight = dec.getLength(getLengthValue("min-height"), false, -1, -1, container.mainSize);
            if(minheight != -1) {
                //minheight nenastaveno
                if(style.getProperty("min-height") == CSSProperty.MinHeight.percentage && container.mainSize == 0)
                    return content.height;
                if (minheight < content.height)
                    if(minheight > hypotheticalMainSize)
                        content.height = minheight;
                    else
                        content.height = hypotheticalMainSize;
            }
        }


        return content.height;
    }

    protected int boundByWidth(FlexContainerBlockBox container){
        CSSDecoder dec = new CSSDecoder(ctx);
        int width = dec.getLength(getLengthValue("width"), false, -1, -1, container.mainSize);
        if(width != -1){
            //height nenastaveno
            if(style.getProperty("width") == CSSProperty.Width.percentage && container.mainSize == 0)
                return content.width;

            if(width < content.width) {
                if(width > hypotheticalMainSize)
                    content.width = width;
                else
                    content.width = hypotheticalMainSize;
            }
        } else {
            int minwidth = dec.getLength(getLengthValue("min-width"), false, -1, -1, container.mainSize);
            if(minwidth != -1) {
                //minheight nenastaveno
                if(style.getProperty("min-width") == CSSProperty.MinWidth.percentage && container.mainSize == 0)
                    return content.width;
                if (minwidth < content.width)
                    if(minwidth > hypotheticalMainSize)
                        content.width = minwidth;
                    else
                        content.width = hypotheticalMainSize;
            }
        }


        return content.width;
    }
}
