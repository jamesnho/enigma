package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author James Nho Nguyen
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _perm = perm;
        _notches = notches;
    }

    @Override
    void advance() {
        int nextSetting = setting() + 1;
        set(permutation().wrap(nextSetting));
    }

    @Override
    String notches() {
        return _notches;
    }

    @Override
    boolean rotates() {
        return true;
    }

    /** Permutation. */
    private Permutation _perm;
    /** Notches. */
    private String _notches;
}
