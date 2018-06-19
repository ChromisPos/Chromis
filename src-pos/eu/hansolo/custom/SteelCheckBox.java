package eu.hansolo.custom;

/**
 *
 * @author hansolo
 */
public class SteelCheckBox extends javax.swing.JCheckBox
{
    // <editor-fold defaultstate="collapsed" desc="Variable declaration">
    private boolean colored = true;
    private boolean rised = true;
    private eu.hansolo.custom.ColorDef selectedColor = eu.hansolo.custom.ColorDef.JUG_GREEN;
    protected static final String COLORED_PROPERTY = "colored";
    protected static final String COLOR_PROPERTY = "color";
    protected static final String RISED_PROPERTY = "rised";
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public SteelCheckBox()
    {
        super();
        setPreferredSize(new java.awt.Dimension(180, 30));
        setFont(new java.awt.Font("Arial", 0, 12));
        setMaximumSize(new java.awt.Dimension(0, 25));
        setMinimumSize(new java.awt.Dimension(0, 0));
        setBounds(10, 20, 180, 30);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getter/Setter">
    public boolean isColored()
    {
        return this.colored;
    }

    public void setColored(final boolean COLORED)
    {
        final boolean OLD_STATE = this.colored;
        this.colored = COLORED;
        firePropertyChange(COLORED_PROPERTY, OLD_STATE, COLORED);
        repaint();
    }

    public boolean isRised()
    {
        return this.rised;
    }

    public void setRised(final boolean RISED)
    {
        final boolean OLD_VALUE = this.rised;
        this.rised = RISED;
        firePropertyChange(RISED_PROPERTY, OLD_VALUE, RISED);
    }

    public eu.hansolo.custom.ColorDef getSelectedColor()
    {
        return this.selectedColor;
    }

    public void setSelectedColor(final eu.hansolo.custom.ColorDef SELECTED_COLOR)
    {
        final eu.hansolo.custom.ColorDef OLD_COLOR = this.selectedColor;
        this.selectedColor = SELECTED_COLOR;
        firePropertyChange(COLOR_PROPERTY, OLD_COLOR, SELECTED_COLOR);
        repaint();
    }

    @Override
    public void setUI(final javax.swing.plaf.ButtonUI BUI)
    {
        super.setUI(new SteelCheckBoxUI(this));
    }

    public void setUi(final javax.swing.plaf.ComponentUI UI)
    {
        this.ui = new SteelCheckBoxUI(this);
    }

    @Override
    protected void setUI(final javax.swing.plaf.ComponentUI UI)
    {
        super.setUI(new SteelCheckBoxUI(this));
    }
    // </editor-fold>

    @Override
    public String toString()
    {
        return "SteelCheckBox";
    }
}
