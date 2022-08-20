package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.HashMap;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author James Nho Nguyen
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigmaMachine = readConfig();
        setUp(enigmaMachine, _input.nextLine());
        String conMes = "";
        while (_input.hasNext()) {
            String eachLine = _input.nextLine();
            if (eachLine.isBlank()) {
                eachLine = _input.nextLine();
                _output.println();
            }
            if (eachLine.contains("*")) {
                enigmaMachine.resetRotors();
                setUp(enigmaMachine, eachLine);
                conMes = "";
            } else {
                conMes += enigmaMachine.convert(eachLine.replaceAll(" ", ""));
                printMessageLine(conMes);
                conMes = "";
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.nextLine().trim());
            int numRotors = _config.nextInt();
            int numPawls = _config.nextInt();
            ArrayList<Rotor> allRotorsList;
            allRotorsList = new ArrayList<Rotor>();
            if (!_config.hasNext()) {
                throw new EnigmaException("No more rotors in config.");
            }
            while (_config.hasNext()) {
                allRotorsList.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotorsList);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rName;
            String rFirst;
            rName = _config.next();
            rFirst = _config.next();
            String allCycles = "";
            Permutation rotorPerm;
            while (_config.hasNext("\\(.*") && _config.hasNextLine()) {
                allCycles += _config.next() + " ";
            }
            rotorPerm = new Permutation(allCycles, _alphabet);
            if (rFirst.charAt(0) == 'R') {
                return new Reflector(rName, rotorPerm);
            } else if (rFirst.charAt(0) == 'N') {
                return new FixedRotor(rName, rotorPerm);
            } else {
                return new MovingRotor(rName, rotorPerm, rFirst.substring(1));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        int numRotors = M.numRotors();
        Scanner setting = new Scanner(settings);
        if (!setting.next().equals("*")) {
            throw new EnigmaException("Input error: Does not start setting.");
        }
        ArrayList<String> allRotors = new ArrayList<String>();
        for (int eachRotor = 0; eachRotor < numRotors; eachRotor++) {
            allRotors.add(setting.next());
        }
        for (int i = 1; i < allRotors.size(); i++) {
            if (allRotors.get(i).equals("B")) {
                throw new EnigmaException("Ref wrong place");
            }
        }
        String[] convertedRotors = new String[M.numRotors()];
        for (int eachRA = 0; eachRA < allRotors.size(); eachRA++) {
            convertedRotors[eachRA] = allRotors.get(eachRA);
        }
        checkRotorsExist(convertedRotors, M);
        M.insertRotors(convertedRotors);
        String rotorSetting = setting.next();
        M.setRotors(rotorSetting);
        String rotorCycles = "";
        while (setting.hasNext("[^\\w]\\([^\\)]+\\)")) {
            rotorCycles += setting.next();
        }
        Permutation machineP = new Permutation(rotorCycles, _alphabet);
        M.setPlugboard(machineP);
    }
    private void checkRotorsExist(String[] convertedRotors, Machine M) {
        HashMap<String, Rotor> map = M.getRotorMap();
        for (String rotor: convertedRotors) {
            if (!map.containsKey(rotor)) {
                throw new EnigmaException("Bad rotor name");
            }
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String group = "";
        while (msg.length() > 5) {
            group += msg.substring(0, 5) + " ";
            msg = msg.substring(5);
        }
        group += msg;
        _output.println(group);
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
