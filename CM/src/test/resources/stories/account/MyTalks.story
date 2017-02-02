
Narrative:
As a Speaker
I would like to have an ability to submit a new talk.
This feature will be available for speakers who have filled in 'My Info' page
otherwise they will be prompted to fill in their personal info first.


Scenario: Speaker didnt filled My info majority fields

Given user logged as speaker accessing 'My Talks' page:
|email                  |password|
|mytalksEmpty@tester.com|tester  |
When user clicks on 'Submit New Talk' button
Then pop up is shown with text 'Please fill out all required info on the My Info page before submitting a talk.'
And clicks OK button



Scenario: Submit new Talk with empty fields

Given user logged as speaker accessing 'My Talks' page:
|email             |password|
|mytalks@tester.com|tester  |
When user clicks on 'Submit New Talk' button
And clicks 'Submit' button
Then pop-up window 'Please fill in all mandatory fields.' is shown
And all fields are highlighted in red



Scenario: Fill invalid data in all field

Given user logged as speaker accessing 'My Talks' page:
|email             |password|
|mytalks@tester.com|tester  |
When user clicks on 'Submit New Talk' button
And user fills data in 'Title','Description' and 'Additional Info':
|title  |description  |additionalInfo  |
|<title>|<description>|<additionalInfo>|
Then data are not filled. System doesn't accept invalid data:
|title  |description  |additionalInfo  |
|<title>|<description>|<additionalInfo>|

Examples:
|<title>|<description>|<additionalInfo>|
|251    |3001         |1501            |



Scenario: Fill valid data in all field

Given user logged as speaker accessing 'My Talks' page:
|email             |password|
|mytalks@tester.com|tester  |
When user clicks on 'Submit New Talk' button
And user fills data in 'Title','Description' and 'Additional Info':
|title  |description  |additionalInfo  |
|<title>|<description>|<additionalInfo>|
And choose Topic, Type, Language, Level dropdown menu:
|topic  |type  |language  |level  |
|<topic>|<type>|<language>|<level>|
And clicks 'Submit' button
Then talk appears in grid and have 'New' status
And email was sent to users email :
|email             |
|mytalks@tester.com|
And with subject 'Your talk's status has been updated'
And body contains 'Thank you for your submitted talk. It has been successfully registered in our system. We will review it and send an additional info soon.'

Examples:
|<title>|<description>|<additionalInfo>|<topic>|<type>|<language>|<level>|
|10     |10           |10              |1      |1     |1         |1      |
|15     |12           |12              |2      |2     |2         |2      |
|15     |12           |12              |2      |2     |2         |2      |



Scenario: Fill valid data in all field not saving it

Given user logged as speaker accessing 'My Talks' page:
|email             |password|
|mytalks@tester.com|tester  |
When user clicks on 'Submit New Talk' button
And user fills data in 'Title','Description' and 'Additional Info':
|title  |description  |additionalInfo  |
|<title>|<description>|<additionalInfo>|
And choose Topic, Type, Language, Level dropdown menu:
|topic  |type  |language  |level  |
|<topic>|<type>|<language>|<level>|
And clicks 'Exit' button
Then info msg is shown saying 'Are you sure you want to leave the window?',
 'Click 'Yes' to leave and all your changes will lost.',
  'Click 'No' to return and Submit/Update your changes'
And click 'Yes' button

Examples:
|<title>|<description>|<additionalInfo>|<topic>|<type>|<language>|<level>|
|10     |10           |10              |1      |1     |1         |1      |

Scenario: speaker can view approved talk
Given user logged as speaker accessing 'My Talks' page:
|email              |password|
|speaker@speaker.com|speaker |
And creates new Talk:
|title  |description  |additionalInfo  |topic  |type  |language  |level  |
|<title>|<description>|<additionalInfo>|<topic>|<type>|<language>|<level>|
And user logged as organiser 'Talks' page:
|email              |password |
|organiser@gmail.com|organiser|
When organiser clicks new created Talk:
|status|
|New   |
And clicks approve button after filling comment:
|comment  |
|<comment>|
And user log in as speaker accessing 'My Talks' page:
|email              |password|
|speaker@speaker.com|speaker |
And speaker clicks new created Talk:
|status  |
|Approved|
Then all fields are read-only for speaker

Examples:
|<title>    |<description>|<additionalInfo>|<topic>|<type>|<language>|<level>|<comment>|
|TestMyTalks|10           |10              |1      |1     |1         |1      |comment  |

Scenario: speaker can view rejected talk
Given user logged as speaker accessing 'My Talks' page:
|email              |password|
|speaker@speaker.com|speaker |
And creates new Talk:
|title  |description  |additionalInfo  |topic  |type  |language  |level  |
|<title>|<description>|<additionalInfo>|<topic>|<type>|<language>|<level>|
And user logged as organiser 'Talks' page:
|email              |password |
|organiser@gmail.com|organiser|
When organiser clicks new created Talk:
|status|
|New   |
And clicks reject button after filling comment:
|comment  |
|<comment>|
And user log in as speaker accessing 'My Talks' page:
|email              |password|
|speaker@speaker.com|speaker |
And speaker clicks new created Talk:
|status  |
|Rejected|
Then all fields are read-only for speaker

Examples:
|<title>    |<description>|<additionalInfo>|<topic>|<type>|<language>|<level>|<comment>|
|TestMyTalks|10           |10              |1      |1     |1         |1      |comment  |

