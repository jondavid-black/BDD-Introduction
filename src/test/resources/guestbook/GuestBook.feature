Feature: Users want to sign the guest book and see who else has signed
    Everybody wants to say I was here and see who else has been here

    @add
    Scenario: Add a entry to the Guest Book
        Given the GuestBook service is running
        And the GuestBook is empty
        When the user sends request to add a guest entry
        Then the GuestBook contians our entry

    @read
    Scenario: List entries in the Guest Book
        Given the GuestBook service is running
        And the GuestBook is empty
        And there are 5 entries in the GuestBook
        When the user requests the list of GuestBook entries
        Then the GuestBook returns 5 entries

