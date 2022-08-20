package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author James Nho Nguyen
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        _listCycles = cycles.split(" ");
        for (int each = 0; each < _alphabet.size(); each++) {
            char alpha = _alphabet.toChar(each);
            int startLeft;
            int startRight;
            if (_cycles.indexOf(alpha) != -1) {
                startLeft = _cycles.indexOf(alpha);
                startRight = _cycles.lastIndexOf(alpha);
                if (startRight != startLeft) {
                    throw new EnigmaException("Duplicate letters Cycle");
                }
            }
        }
    }
    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles += " " + cycle;
        _listCycles = _cycles.split(" ");
    }
    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }
    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }
    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int wrapPerm = wrap(p);
        char intToChar = _alphabet.toChar(wrapPerm);
        return _alphabet.toInt(permute(intToChar));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int wrapInvert = wrap(c);
        char intToChar = _alphabet.toChar(wrapInvert);
        return _alphabet.toInt(invert(intToChar));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int counter = 0;
        for (String eachCycle: _listCycles) {
            if (eachCycle.indexOf(p) > -1) {
                int charToInt = eachCycle.indexOf(p);
                char intToChar;
                if (charToInt == eachCycle.length() - 2) {
                    charToInt = 1;
                    intToChar = eachCycle.charAt(charToInt);
                    return intToChar;
                } else {
                    charToInt += 1;
                    intToChar = eachCycle.charAt(charToInt);
                    return intToChar;
                }
            } else if (eachCycle.indexOf(p) == -1) {
                counter += 1;
            } else {
                int charToInt = eachCycle.indexOf(p);
                charToInt++;
                char intToChar = eachCycle.charAt(charToInt);
                return intToChar;
            }
        }
        if (counter == _listCycles.length) {
            return p;
        }
        return p;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int counter = 0;
        for (String eachCycle : _listCycles) {
            if (eachCycle.indexOf(c) > -1) {
                int charToInt = eachCycle.indexOf(c);
                char intToChar;
                if (charToInt == 1) {
                    charToInt = eachCycle.length() - 2;
                    intToChar = eachCycle.charAt(charToInt);
                    return intToChar;
                } else {
                    charToInt -= 1;
                    intToChar = eachCycle.charAt(charToInt);
                    return intToChar;
                }
            } else if (eachCycle.indexOf(c) == -1) {
                counter += 1;
            }
            if (counter == _listCycles.length) {
                return (c);
            }
        } return c;
    }
    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }
    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int notInCycleCounter = 0;
        for (int eachL = 0; eachL < _alphabet.size(); eachL++) {
            char eachLetter = _alphabet.toChar(eachL);
            for (String eachCycle: _listCycles) {
                if (eachCycle.indexOf(eachLetter) == -1) {
                    notInCycleCounter++;
                }
            }
        }
        return notInCycleCounter == 0;
    }
    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Cycles. */
    private String _cycles;
    /** List of Cycles. */
    private String[] _listCycles;
}
