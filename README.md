# Filter Keyword Search & Attach CSV of Results on an Existing Jira ticket


## Description

Follow the set up directions in the Setup Instructions section to run this script. This script will allow you to find any filters that contain a certain keyword (not in the title but within the filter jql itself). This data is then exported into a CSV file and attached to an existing Jira ticket.

## Table of Contents
* [Setup Instructions](#setup-instructions)
* [Usage](#usage)
* [Permissions](#permissions)
* [Limitations](#limitations)
* [Credits](#credits)


## Setup Instructions

Here are the setup steps:

1. Ensure you have ScriptRunner installed from the Atlassian Marketplace.

2. Add the following values to this script:

````
def issueKey = "TIPA-1" //this is the issue you want the CSV to be attached to

def SEARCH_KEYWORD = 'done' //this is the keyword or phrase you want to search within the filter JQL, it is NOT case sensitive
````

## Usage

To use this script, run it in the ScriptRunner script console.

## Permissions 

No specific permissions are needed for this script to work, but please note that the filters returned depend on the following:

Only the following filters that match the query parameters are returned:

- filters owned by the user.
- filters shared with a group that the user is a member of.
- filters shared with a private project that the user has Browse projects project permission for.
- filters shared with a public project.
- filters shared with the public.

Therefore, this script cannot guarantee that ALL filters that meet the search criteria are returned, but any available based on the above limitations to the API endpoints.

However, note that ScriptRunner requires you to be a Jira admin to access the script console.

## Limitations

The only limitations are the following:

- filters are returned based off of permissions (see the Permissions section)
- the amount of filters searched for (up to 3,000), but this can be fixed by adding on to the index array

## Credits

This was created by anicrob. 

Jira Cloud REST APIs Endpoint used:

- [Search for Filters](https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-filters/#api-rest-api-3-filter-search-get)


You can find more of my work at [anicrob](https://github.com/anicrob).
