package seedu.addressbook.commands;

import org.junit.Test;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.util.TypicalPersons;

import java.util.*;

import static org.junit.Assert.*;

public class EditCommandTest {
    private final List<ReadOnlyPerson> LAST_SHOWN_LIST;
    private static final int TARGET_INDEX = 1;

    private static final Set<String> EMPTY_STRING_SET = Collections.emptySet();

    public EditCommandTest() {
        LAST_SHOWN_LIST = new ArrayList<>();
        LAST_SHOWN_LIST.add(new TypicalPersons().getTypicalPersons()[0]);
        LAST_SHOWN_LIST.add(new TypicalPersons().getTypicalPersons()[1]);
    }

    @Test
    public void editCommand_invalidName_throwsException() {
        final String[] invalidNames = { "", " ", "[]\\[;]" };
        for (String name : invalidNames) {
            assertConstructingInvalidEditCmdThrowsException(name, Phone.EXAMPLE, true, Email.EXAMPLE, false,
                    Address.EXAMPLE, true, EMPTY_STRING_SET);
        }
    }

    @Test
    public void addCommand_invalidPhone_throwsException() {
        final String[] invalidNumbers = { "", " ", "1234-5678", "[]\\[;]", "abc", "a123", "+651234" };
        for (String number : invalidNumbers) {
            assertConstructingInvalidEditCmdThrowsException(Name.EXAMPLE, number, false, Email.EXAMPLE, true,
                    Address.EXAMPLE, false, EMPTY_STRING_SET);
        }
    }

    @Test
    public void addCommand_invalidEmail_throwsException() {
        final String[] invalidEmails = { "", " ", "def.com", "@", "@def", "@def.com", "abc@",
                                         "@invalid@email", "invalid@email!", "!invalid@email" };
        for (String email : invalidEmails) {
            assertConstructingInvalidEditCmdThrowsException(Name.EXAMPLE, Phone.EXAMPLE, false, email, false,
                    Address.EXAMPLE, false, EMPTY_STRING_SET);
        }
    }

    @Test
    public void addCommand_invalidAddress_throwsException() {
        final String[] invalidAddresses = { "", " " };
        for (String address : invalidAddresses) {
            assertConstructingInvalidEditCmdThrowsException(Name.EXAMPLE, Phone.EXAMPLE, true, Email.EXAMPLE,
                    true, address, true, EMPTY_STRING_SET);
        }
    }

    @Test
    public void addCommand_invalidTags_throwsException() {
        final String[][] invalidTags = { { "" }, { " " }, { "'" }, { "[]\\[;]" }, { "validTag", "" },
                                         { "", " " } };
        for (String[] tags : invalidTags) {
            Set<String> tagsToAdd = new HashSet<>(Arrays.asList(tags));
            assertConstructingInvalidEditCmdThrowsException(Name.EXAMPLE, Phone.EXAMPLE, true, Email.EXAMPLE,
                    true, Address.EXAMPLE, false, tagsToAdd);
        }
    }

    /**
     * Asserts that attempting to construct an edit command with the supplied
     * invalid data throws an IllegalValueException
     */
    private void assertConstructingInvalidEditCmdThrowsException(String name, String phone,
            boolean isPhonePrivate, String email, boolean isEmailPrivate, String address,
            boolean isAddressPrivate, Set<String> tags) {
        try {
            new EditCommand(LAST_SHOWN_LIST, TARGET_INDEX, name,
                    phone, isPhonePrivate, email, isEmailPrivate, address, isAddressPrivate, tags);
        } catch (IllegalValueException e) {
            return;
        }
        String error = String.format(
                "An add command was successfully constructed with invalid input: %s %s %s %s %s %s %s %s",
                name, phone, isPhonePrivate, email, isEmailPrivate, address, isAddressPrivate, tags);
        fail(error);
    }

    @Test
    public void editCommand_validData_correctlyEdited() throws Exception {
        EditCommand command = new EditCommand(LAST_SHOWN_LIST, TARGET_INDEX, Name.EXAMPLE,
                Phone.EXAMPLE, true, Email.EXAMPLE, false,
                Address.EXAMPLE, true, EMPTY_STRING_SET);
        ReadOnlyPerson p = command.getEditedPerson();

        // TODO: add comparison of tags to person.equals and equality methods to
        // individual fields that compare privacy to simplify this
        assertEquals(Name.EXAMPLE, p.getName().fullName);
        assertEquals(Phone.EXAMPLE, p.getPhone().value);
        assertTrue(p.getPhone().isPrivate());
        assertEquals(Email.EXAMPLE, p.getEmail().value);
        assertFalse(p.getEmail().isPrivate());
        assertEquals(Address.EXAMPLE, p.getAddress().value);
        assertTrue(p.getAddress().isPrivate());
        boolean isTagListEmpty = !p.getTags().iterator().hasNext();
        assertTrue(isTagListEmpty);
    }

    @Test
    public void editCommand_addressBookAlreadyContainsPerson_throwsException() throws Exception {
        AddressBook book = new AddressBook();
        book.addPerson(new TypicalPersons().getTypicalPersons()[0]);
        book.addPerson(new TypicalPersons().getTypicalPersons()[1]);

        EditCommand command = new EditCommand(LAST_SHOWN_LIST, TARGET_INDEX,
                        new TypicalPersons().getTypicalPersons()[1]);
        command.setData(book, LAST_SHOWN_LIST);
        CommandResult result = command.execute();

        assertEquals(AddCommand.MESSAGE_DUPLICATE_PERSON, result.feedbackToUser);
    }
}
