package net.bitpot.injector.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Class should be set to JTextField document to limit typing characters.
 */
public class JTextFieldLimit extends PlainDocument
{
    private int limit;
    // optional uppercase conversion
    private boolean toUppercase = false;

    JTextFieldLimit(int limit)
    {
        super();
        this.limit = limit;
    }

    JTextFieldLimit(int limit, boolean upper)
    {
        super();
        this.limit = limit;
        toUppercase = upper;
    }

    public void insertString
            (int offset, String str, AttributeSet attr)
            throws BadLocationException
    {
        if (str == null) return;

        if ((getLength() + str.length()) <= limit)
        {
            if (toUppercase) str = str.toUpperCase();
            super.insertString(offset, str, attr);
        }
    }
}



