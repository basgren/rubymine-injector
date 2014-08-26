package net.bitpot.injector.handlers.checkers;

/**
 * Created by IntelliJ IDEA.
 * User: Basil Gren
 * Date: 30.04.11
 * Time: 22:48
 * Provides custom methods for injection checkers.
 */
public class CustomChecker
{

    public static int posForward(CharSequence seq, String findStr, int startPos)
    {
        int seekLen = findStr.length();
        int seqLen = seq.length();
        int i, id;

        char seekChar = findStr.charAt(0);

        while (startPos < seqLen)
        {
            if (seq.charAt(startPos) == seekChar)
            {
                if (seekLen == 1)
                    return startPos;

                i = 1;
                while (i < seekLen)
                {
                    id = startPos + i;
                    if (id >= seqLen)
                       break;

                    if (seq.charAt(id) != findStr.charAt(i))
                        break;

                    i++;
                }

                if (i == seekLen)
                    return startPos;
            }

            startPos++;
        }

        return -1;
    }



    public static int posBackward(CharSequence seq, String findStr, int startPos)
    {
        int seekLen = findStr.length();
        int i, id;

        char seekChar = findStr.charAt(seekLen - 1);

        // Check position. It shouldn't be more than document length.
        if (startPos >= seq.length())
            startPos = seq.length() - 1;

        // If start position is less than length of search string, we cannot find it in any case.
        if (startPos < seekLen - 1)
            return -1;

        while (startPos >= 0)
        {
            if (seq.charAt(startPos) == seekChar)
            {
                if (seekLen == 1)
                    return startPos;

                i = seekLen - 2;
                while (i >= 0)
                {
                    id = startPos - i - 1;
                    if (id < 0)
                       break;

                    if (seq.charAt(id) != findStr.charAt(i))
                        break;

                    i--;
                }

                if (i < 0)
                    return startPos - seekLen + 1;
            }

            startPos--;
        }

        return -1;
    }

}
