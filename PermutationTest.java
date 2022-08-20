package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author James Nho Nguyen
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void testNotInCycle() {
        Permutation notInCycle = new Permutation("(BACD)",
                new Alphabet("ABCDF"));
        assertEquals('F', notInCycle.invert('F'));
    }
    @Test(expected = EnigmaException.class)
    public void testDuplicateCycle() {
        Permutation p1 = new Permutation("(BACBD)",
                new Alphabet("ABCD"));
        p1.invert('B');
    }
    @Test(expected = EnigmaException.class)
    public void testDuplicateAlphabet() {
        Permutation p2 = new Permutation("(BACD)",
                new Alphabet("ABCBD"));
        p2.invert('B');
    }
    @Test
    public void testPermSize() {
        Permutation testPerm = new Permutation("(ABDC)",
                new Alphabet("ABCD"));
        assertEquals(4, testPerm.size());
    }
    @Test
    public void testPermPermute() {
        Permutation testPerm = new Permutation("(ADCB)",
                new Alphabet("ABCD"));
        assertEquals(1, testPerm.permute(2));
    }
    @Test
    public void testPermPermuteEdge() {
        Permutation testPermEdge = new Permutation("(ABCD)",
                new Alphabet("ABCD"));
        assertEquals(0, testPermEdge.permute(3));
    }
    @Test
    public void testPermInvert() {
        Permutation testInvert = new Permutation("(ACBFED)",
                new Alphabet("ABCDEF"));
        assertEquals(5, testInvert.invert(4));
    }
    @Test
    public void testPermInvertEdge() {
        Permutation testInvertEdge = new Permutation("(ACBFED)",
                new Alphabet("ABCDEF"));
        assertEquals(3, testInvertEdge.invert(0));
    }
    @Test
    public void testPermSimplerPermute() {
        Permutation testSimplerPermute = new Permutation("(ABCDHIJKEFG)",
                new Alphabet("ABCDEFGHIJK"));
        assertEquals('E', testSimplerPermute.permute('K'));
    }
    @Test
    public void testPermSimplerPermuteSingleCycle() {
        Permutation testSimplerInvertEdge = new Permutation("(A)",
                new Alphabet("AB"));
        assertEquals('A', testSimplerInvertEdge.permute('A'));
    }
    @Test
    public void testPermSimplerPermuteSingleClass() {
        Permutation testSimplerInvertEdge = new Permutation("(ABCDE)",
                new Alphabet("ABCDEP"));
        assertEquals('P', testSimplerInvertEdge.permute('P'));
    }
    @Test
    public void testPermSimplerPermuteEdge() {
        Permutation testSimplerPermute = new Permutation("(ABCDHIJKEFG)",
                new Alphabet("ABCDEFGHIJK"));
        assertEquals('A', testSimplerPermute.permute('G'));
    }
    @Test
    public void testPermSimplerInvert() {
        Permutation testSimplerInvertEdge = new Permutation("(ABCDHIJKEFG)",
                new Alphabet("ABCDEFGHIJK"));
        assertEquals('D', testSimplerInvertEdge.invert('H'));
    }
    @Test
    public void testPermSimplerInvertSingleCycle() {
        Permutation testSimplerInvertEdge = new Permutation("(A)",
                new Alphabet("A"));
        assertEquals('A', testSimplerInvertEdge.invert('A'));
    }
    @Test
    public void testPermSimplerInvertSingleClass() {
        Permutation testSimplerInvertEdge = new Permutation("(ABCDE)",
                new Alphabet("ABCDEP"));
        assertEquals('P', testSimplerInvertEdge.invert('P'));
    }
    @Test
    public void testPermSimplerInvertEdge() {
        Permutation testSimplerInvertEdge = new Permutation("(ABCDHIJKEFG)",
                new Alphabet("ABCDEFGHIJK"));
        assertEquals('G', testSimplerInvertEdge.invert('A'));
    }
    @Test
    public void testingMultipleCycle() {
        Permutation testMulCycle = new Permutation("(ABCD) (EFGHI)",
                new Alphabet("ABCDEFGHIJK"));
        assertEquals('K', testMulCycle.permute('K'));
    }
    @Test
    public void testingMultipleCycle2() {
        Permutation testMulCycle = new Permutation("(ABCD) (EFGHI)",
                new Alphabet("ABCDEFGHIJK"));
        assertEquals('F', testMulCycle.permute('E'));
    }
    @Test
    public void notDerangementTest() {
        Permutation derangementTest1 = new Permutation("(ABCD) (EFGH)",
                new Alphabet("ABCDEFGHI"));
        assertFalse(derangementTest1.derangement());
    }
    @Test
    public void isDerangementTest() {
        Permutation derangementTest2 = new Permutation("(ABCD) (EFGH)",
                new Alphabet("ABCDEFGH"));
        assertFalse(derangementTest2.derangement());
    }
}
