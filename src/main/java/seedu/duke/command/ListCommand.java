package seedu.duke.command;

import seedu.duke.classes.Expense;
import seedu.duke.classes.Income;
import seedu.duke.classes.StateManager;
import seedu.duke.classes.Transaction;
import seedu.duke.exception.DukeException;
import seedu.duke.ui.Ui;

import java.util.ArrayList;
import java.util.HashMap;

public class ListCommand extends Command {
    private static final String INVALID_TYPE_FORMAT = "I'm sorry, you need to specify a type in the format " +
            "'/type in' or '/type out'";
    private static final String INVALID_GOAL_FORMAT = "You have entered /goal, but you have entered an invalid goal";
    private static final String INVALID_CATEGORY_FORMAT = "You have entered /category, but you have entered an " +
            "invalid category";
    private static final String EMPTY_LIST = "It appears that we have came up empty. Why not try adding some" +
            " transactions first?";
    private static final String[] IN_HEADERS = {"ID", "Description", "Date", "Amount", "Goal"};
    private static final String[] OUT_HEADERS = {"ID", "Description", "Date", "Amount", "Category"};
    private static final String IN = "IN TRANSACTIONS";
    private static final String OUT = "OUT TRANSACTIONS";
    private Ui ui;

    public ListCommand(String description, HashMap<String, String> args) {
        super(description, args);
    }

    @Override
    public void execute(Ui ui) throws DukeException {
        this.ui = ui;
        validateArgs();
        listTypeHandler();
    }

    // Description gets ignored for list
    private void validateArgs() throws DukeException {
        if (!getArgs().containsKey("type")) {
            throw new DukeException(INVALID_TYPE_FORMAT);
        }

        String type = getArg("type");
        if (!type.equals("in") && !type.equals("out")) {
            throw new DukeException(INVALID_TYPE_FORMAT);
        }

        if (getArgs().containsKey("goal") && getArgs().containsKey("category")) {
            throw new DukeException("You can't use both /goal and /category");
        }

        if (getArgs().containsKey("goal") && getArg("goal").isBlank()) {
            throw new DukeException(INVALID_GOAL_FORMAT);
        }

        if (getArgs().containsKey("goal")) {
            String goal = getArg("goal");
            int result = StateManager.getStateManager().getGoalIndex(goal);
            if (result == -1) {
                throw new DukeException(INVALID_GOAL_FORMAT);
            }

        }

        if (getArgs().containsKey("category") && getArg("category").isBlank()) {
            throw new DukeException(INVALID_CATEGORY_FORMAT);
        }

        if (getArgs().containsKey("category")) {
            String goal = getArg("category");
            int result = StateManager.getStateManager().getCategoryIndex(goal);
            if (result == -1) {
                throw new DukeException(INVALID_CATEGORY_FORMAT);
            }

        }
    }

    private void listTypeHandler() throws DukeException {
        String type = getArg("type");
        assert type != null;
        if (type.equals("in")) {
            listIncome();
        } else {
            listExpenses();
        }
    }

    private void printList(ArrayList<ArrayList<String>> listArray, String headerMessage) {
        if (headerMessage.equals(IN)) {
            ui.listTransactions(listArray, IN_HEADERS, headerMessage);
        } else {
            ui.listTransactions(listArray, OUT_HEADERS, headerMessage);
        }

    }

    private void listIncome() throws DukeException {
        String filterGoal = null;
        if(getArgs().containsKey("goal")) {
            filterGoal = getArg("goal").toLowerCase();
        }
        ArrayList<Income> incomeArray = StateManager.getStateManager().getAllIncomes();
        ArrayList<ArrayList<String>> printIncomes = new ArrayList<>();
        if (incomeArray == null || incomeArray.isEmpty()) {
            throw new DukeException(EMPTY_LIST);
        }
        int index = 1;
        for (Income i : incomeArray) {
            String goal = i.getGoal().getDescription();
            if (filterGoal == null || filterGoal.equalsIgnoreCase(goal)) {
                ArrayList<String> transaction = formatTransaction(i.getTransaction(), index);
                transaction.add(goal);
                printIncomes.add(transaction);
                index++;
            }
        }
        printList(printIncomes, IN);

    }

    private void listExpenses() throws DukeException {
        String filterCategory = null;
        if(getArgs().containsKey("category")) {
            filterCategory = getArg("category").toLowerCase();
        }
        ArrayList<Expense> expenseArray = StateManager.getStateManager().getAllExpenses();
        ArrayList<ArrayList<String>> printExpenses = new ArrayList<>();
        if (expenseArray == null || expenseArray.isEmpty()) {
            throw new DukeException(EMPTY_LIST);
        }
        int index = 1;
        for (Expense i : expenseArray) {
            String category = i.getCategory().getName();
            if(filterCategory == null || filterCategory.equalsIgnoreCase(category)) {
                ArrayList<String> transaction = formatTransaction(i.getTransaction(), index);
                transaction.add(i.getCategory().getName());
                printExpenses.add(transaction);
                index++;
            }
        }
        printList(printExpenses, OUT);
    }

    private ArrayList<String> formatTransaction(Transaction transaction, int index) {
        ArrayList<String> transactionStrings = new ArrayList<>();
        transactionStrings.add(String.valueOf(index));
        transactionStrings.add(transaction.getDescription());
        transactionStrings.add(transaction.getDate().toString());
        transactionStrings.add(String.valueOf(ui.formatAmount(transaction.getAmount())));
        return transactionStrings;
    }
}
