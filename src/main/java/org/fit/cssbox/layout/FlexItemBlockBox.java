package org.fit.cssbox.layout;

import cz.vutbr.web.css.*;
import org.w3c.dom.Element;

import java.awt.*;

public class FlexItemBlockBox extends BlockBox  implements Comparable  {

    public static final CSSProperty.AlignSelf ALIGN_SELF_AUTO = CSSProperty.AlignSelf.Auto;
    public static final CSSProperty.AlignSelf ALIGN_SELF_FLEX_START = CSSProperty.AlignSelf.FlexStart;
    public static final CSSProperty.AlignSelf ALIGN_SELF_FLEX_END = CSSProperty.AlignSelf.FlexEnd;
    public static final CSSProperty.AlignSelf ALIGN_SELF_CENTER = CSSProperty.AlignSelf.Center;
    public static final CSSProperty.AlignSelf ALIGN_SELF_BASELINE = CSSProperty.AlignSelf.Baseline;
    public static final CSSProperty.AlignSelf ALIGN_SELF_STRETCH = CSSProperty.AlignSelf.Stretch;

    public static final CSSProperty.FlexBasis FLEX_BASIS_CONTENT = CSSProperty.FlexBasis.CONTENT;
    public static final CSSProperty.FlexBasis FLEX_BASIS_LENGTH = CSSProperty.FlexBasis.length;
    public static final CSSProperty.FlexBasis FLEX_BASIS_PERCENTAGE = CSSProperty.FlexBasis.percentage;
    public static final CSSProperty.FlexBasis FLEX_BASIS_AUTO = CSSProperty.FlexBasis.AUTO;

    public static final CSSProperty.FlexGrow FLEX_GROW_NUMBER = CSSProperty.FlexGrow.number;

    public static final CSSProperty.FlexShrink FLEX_SHRINK_NUMBER = CSSProperty.FlexShrink.number;

    public static final CSSProperty.Order ORDER_INTEGER = CSSProperty.Order.integer;


    /** flex basis specified by the style */
                                                //INITIALS
    protected CSSProperty.AlignSelf alignSelf; //auto
    protected CSSProperty.FlexBasis flexBasis; // auto
    protected CSSProperty.FlexGrow flexGrow; // 0
    protected CSSProperty.FlexShrink flexShrink; // 1
    protected CSSProperty.Order flexOrder; //0

    protected float flexGrowValue;
    protected float flexShrinkValue;
    protected int flexBasisValue;
    protected int flexOrderValue;

    protected boolean flexBasisSetByCont;

    protected int hypoteticalMainSize;  // to jest final flex-basis  (flex-basis ukotvená MIN a MAX width)

    public FlexItemBlockBox(Element n, Graphics2D g, VisualContext ctx) {
        super(n, g, ctx);
        isblock = true;
        flexBasisValue = 0;
        flexBasisSetByCont = false;
    }

    public FlexItemBlockBox(InlineBox src) {
        super(src);
        isblock = true;
        flexBasisValue = 0;
        flexBasisSetByCont = false;
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
        if (alignSelf == null) alignSelf = CSSProperty.AlignSelf.Auto;

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
        Term<?> len = style.getValue("order", false);
        if(len != null) {
            flexOrderValue = ((TermInteger) len).getValue().intValue();
        } else {
            flexOrderValue = 0;
        }

    }

    protected void disableFloats(){
        floatY = 0;
        floatXl = 0;
        floatXr = 0;
    }

    protected int setFlexBasisValue(FlexContainerBlockBox parentContainer){
        CSSDecoder dec = new CSSDecoder(parentContainer.ctx);
        int contw = parentContainer.getContentWidth();

        if(flexBasis == CSSProperty.FlexBasis.valueOf("length") || flexBasis == CSSProperty.FlexBasis.valueOf("percentage")) {
            //used flex-basis
            if(flexBasis == CSSProperty.FlexBasis.valueOf("percentage") && !parentContainer.isDirectionRow) {
                flexBasisSetByCont = true;
            } else {
                flexBasisValue = dec.getLength(getLengthValue("flex-basis"), false, 0, 0, contw);
            }
        }
        else if (flexBasis == FlexItemBlockBox.FLEX_BASIS_CONTENT){
            //TODO content
            flexBasisSetByCont = true;
        }
        else if (flexBasis == FlexItemBlockBox.FLEX_BASIS_AUTO){
            //TODO: auto -> width/height
            if(parentContainer.isDirectionRow()) {
                if (style.getProperty("width") == CSSProperty.Width.AUTO || style.getProperty("width") == null) {
                    flexBasisSetByCont = true;
                }
                else {
                    flexBasisValue = dec.getLength(getLengthValue("width"), false, 0, 0, contw); //use width
                }
            } else {
                if (style.getProperty("height") == CSSProperty.Height.AUTO  || style.getProperty("height") == null || style.getProperty("height") == CSSProperty.Height.valueOf("percentage")) {
//                    if(style.getProperty("min-height") == null)
//
                    flexBasisSetByCont = true;
                } else {
                    flexBasisValue = dec.getLength(getLengthValue("height"), false, 0, 0, 0); //use height
                }
            }
        }

        if(flexBasisSetByCont)
                setFlexBasisBasedByContent();

        return flexBasisValue;
    }

    protected int boundFlexBasisByMinAndMaxValues(int value) {
        if(((FlexContainerBlockBox) parent).isDirectionRow()) {
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
//        if(contblock)
//            flexBasisValue = getMinimalContentWidth();
//        else
            flexBasisValue = getMaximalContentWidth();
    }


    protected void fixMargins(FlexContainerBlockBox parent){
        CSSDecoder decoder = new CSSDecoder(parent.ctx);

        if(style.getProperty("margin-right") != CSSProperty.Margin.AUTO || style.getProperty("margin") != CSSProperty.Margin.AUTO) {
            margin.right = decoder.getLength(getLengthValue("margin-right"), false, 0, 0, 0);
            emargin.right = margin.right;
        }

        if(style.getProperty("margin-left") != CSSProperty.Margin.AUTO || style.getProperty("margin") != CSSProperty.Margin.AUTO) {
            margin.left = decoder.getLength(getLengthValue("margin-left"), false, 0, 0, 0);
            emargin.left = margin.left;
        }

        if(style.getProperty("margin-top") != CSSProperty.Margin.AUTO || style.getProperty("margin") != CSSProperty.Margin.AUTO) {
            margin.top = decoder.getLength(getLengthValue("margin-top"), false, 0, 0, 0);
            emargin.top = margin.top;
        }

        if(style.getProperty("margin-bottom") != CSSProperty.Margin.AUTO || style.getProperty("margin") != CSSProperty.Margin.AUTO) {
            margin.bottom = decoder.getLength(getLengthValue("margin-bottom"), false, 0, 0, 0);
            emargin.bottom = margin.bottom;
        }


    }

    @Override
    public int compareTo(Object o) {
        if(!(o instanceof FlexItemBlockBox))
            return 0;
        int compareOrder = ((FlexItemBlockBox) o).getFlexOrderValue();
        return this.flexOrderValue - compareOrder;
    }
}
