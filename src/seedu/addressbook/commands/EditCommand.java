package seedu.addressbook.commands;

import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.data.tag.Tag;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditCommand extends Command {
    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": edits the data of a person identified by the index number used in the last person listing.\n"
            + "Parameters: INDEX [NAME] [PREFIX/DATA]..."
            + "Example: " + COMMAND_WORD + " 1 John Doe p/12345678 pe/johnd@gmail.com";

    public static final String MESSAGE_EDIT_PERSON_SUCCESS = "Edited Person: %1$s";

    private ReadOnlyPerson target;
    private Person editedPerson;
    private boolean isIndexValid = true;

    public EditCommand(List<? extends ReadOnlyPerson> lastShownList, int targetVisibleIndex, String name,
                       String phone, boolean isPhonePrivate,
                       String email, boolean isEmailPrivate,
                       String address, boolean isAddressPrivate,
                       Set<String> tags) throws IllegalValueException {
        super(targetVisibleIndex);
        this.relevantPersons = lastShownList;

        try {
            target = getTargetPerson();
        } catch (IndexOutOfBoundsException ie) {
            isIndexValid = false;
            return;
        }

        Name newName;
        Phone newPhone;
        Email newEmail;
        Address newAddress;
        final Set<Tag> tagSet;

        if (name != null) {
            newName = new Name(name);
        } else {
            newName = target.getName();
        }
        if (phone != null) {
            newPhone = new Phone(phone, isPhonePrivate);
        } else {
            newPhone = target.getPhone();
        }
        if (email != null) {
            newEmail = new Email(email, isEmailPrivate);
        } else {
            newEmail = target.getEmail();
        }
        if (address != null) {
            newAddress = new Address(address, isAddressPrivate);
        } else {
            newAddress = target.getAddress();
        }
        if (!tags.isEmpty()) {
            tagSet = new HashSet<>();
            for (String tagName : tags) {
                tagSet.add(new Tag(tagName));
            }
        } else {
            tagSet = target.getTags();
        }

        editedPerson = new Person(newName, newPhone, newEmail, newAddress, tagSet);
    }

    public EditCommand(List<? extends ReadOnlyPerson> lastShownList, int targetVisibleIndex, Person p) {
        super(targetVisibleIndex);
        this.relevantPersons = lastShownList;
        this.editedPerson = p;

        try {
            target = getTargetPerson();
        } catch (IndexOutOfBoundsException ie) {
            isIndexValid = false;
            return;
        }
    }


    @Override
    public CommandResult execute() {
        if (!isIndexValid) {
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        try {
            addressBook.editPerson(target, editedPerson);
            relevantPersons = addressBook.getAllPersons().immutableListView();
            return new CommandResult(String.format(MESSAGE_EDIT_PERSON_SUCCESS, editedPerson), relevantPersons);
        } catch (UniquePersonList.PersonNotFoundException pnfe) {
            return new CommandResult(Messages.MESSAGE_PERSON_NOT_IN_ADDRESSBOOK);
        } catch (UniquePersonList.DuplicatePersonException dpe) {
            return new CommandResult(AddCommand.MESSAGE_DUPLICATE_PERSON);
        }
    }

    public Person getEditedPerson() {
        return editedPerson;
    }
}
