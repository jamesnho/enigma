package enigma;
import static enigma.EnigmaException.*;

import java.util.ArrayList;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author James Nho Nguyen
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _alphabetList = new ArrayList<>();
        for (int each = 0; each < chars.length(); each += 1) {
            _alphabetList.add(chars.charAt(each));
        }
        for (int each = 0; each < _alphabetList.size(); each++) {
            if (_alphabetList.lastIndexOf(_alphabetList.get(each)) != each) {
                throw new EnigmaException("Duplicates found.");
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _alphabetList.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _alphabetList.contains(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _alphabetList.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return _alphabetList.indexOf(ch);
    }
    /** Alphabet list. */
    private ArrayList<Character> _alphabetList;
}
