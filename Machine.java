package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author James Nho Nguyen
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _orderedAllRotors = new ArrayList<Rotor>();
        for (Rotor eachRotor: allRotors) {
            _allRotorsHashMap.put(eachRotor.name(), eachRotor);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _orderedAllRotors.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != _numRotors) {
            throw new EnigmaException("Rotor names do not match");
        }
        for (String eachRotor: rotors) {
            if (_allRotorsHashMap.containsKey(eachRotor)) {
                Rotor qualifiedRotor = _allRotorsHashMap.get(eachRotor);
                qualifiedRotor.set(0);
                _orderedAllRotors.add(qualifiedRotor);
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() == (numRotors() - 1)) {
            for (int each = 0; each < setting.length(); each++) {
                char letter = setting.charAt(each);
                Rotor rotor = _orderedAllRotors.get(each + 1);
                rotor.set(letter);
            }
        } else {
            throw new EnigmaException("Length of setting != Rotors");
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard; }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard; }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting())); }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c; }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        ArrayList<Integer> qualifyingIndexes = new ArrayList<Integer>();
        ArrayList<Integer> removedDuplicates = new ArrayList<Integer>();
        for (int eR = _orderedAllRotors.size() - 1; eR > 0; eR--) {
            Rotor rotor = _orderedAllRotors.get(eR);
            if (rotor.atNotch() && _orderedAllRotors.get(eR - 1).rotates()) {
                qualifyingIndexes.add(eR);
                qualifyingIndexes.add(eR - 1);
            }
        }
        qualifyingIndexes.add(_orderedAllRotors.size() - 1);
        for (int eachI = 0; eachI < qualifyingIndexes.size(); eachI++) {
            if (!removedDuplicates.contains(qualifyingIndexes.get(eachI))) {
                removedDuplicates.add(qualifyingIndexes.get(eachI));
            }
        }
        for (int eachAdvancingRotor: removedDuplicates) {
            Rotor rotor = _orderedAllRotors.get(eachAdvancingRotor);
            rotor.advance();
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        Rotor rotor;
        int convert = c;
        for (int eachR = _orderedAllRotors.size() - 1; eachR > 0; eachR--) {
            rotor = _orderedAllRotors.get(eachR);
            convert = rotor.convertForward(convert);
        }
        for (int eachRo = 0; eachRo < _orderedAllRotors.size(); eachRo++) {
            rotor = _orderedAllRotors.get(eachRo);
            convert = rotor.convertBackward(convert);
        }
        return convert;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String convertedMessage = "";
        for (int eachL = 0; eachL < msg.length(); eachL++) {
            int num = alphabet().toInt(msg.charAt(eachL));
            char con = alphabet().toChar(convert(num));
            convertedMessage += con;
        }
        return convertedMessage;
    }

    void resetRotors() {
        _orderedAllRotors.removeAll(_orderedAllRotors);
    }

    /**  Get rotors.
     * @param
     * @return Returns hashmap*/
    HashMap<String, Rotor> getRotorMap() {
        return _allRotorsHashMap;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of rotors. */
    private int _numRotors;
    /** Number of pawls. */
    private int _pawls;
    /** Hashmap of all rotors. */
    private HashMap<String, Rotor> _allRotorsHashMap
            = new HashMap<String, Rotor>();
    /** Array list of all Rotors. */
    private ArrayList<Rotor> _orderedAllRotors;
    /** Plugboard. */
    private Permutation _plugboard;
}
