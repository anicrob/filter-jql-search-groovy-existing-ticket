import org.apache.http.entity.ContentType
import org.slf4j.LoggerFactory

def logger = LoggerFactory.getLogger(this.class)
//this script searches up to 3,000 filters
def index = [
    0, 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750,
    800, 850, 900, 950, 1000, 1050, 1100, 1150, 1200, 1250, 1300, 1350, 1400,
    1450, 1500, 1550, 1600, 1650, 1700, 1750, 1800, 1850, 1900, 1950, 2000,
    2050, 2100, 2150, 2200, 2250, 2300, 2350, 2400, 2450, 2500, 2550, 2600, 2650, 2700, 2750, 2800, 2850, 2900, 2950
  ];
 
//variable to hold the filters that match the criteria
def filterDataMatches = [];

// Variables to fill in:
def issueKey = "TIPA-1" //this is the issue you want the CSV to be attached to
def SEARCH_KEYWORD = 'done' //this is the keyword or phrase you want to search within the filter JQL, it is NOT case sensitive

//make a get call for each index as the filters returned are paginated
index.each { i -> 
    Map<String, Object> response = get("/rest/api/3/filter/search/?maxResults=50&startAt=${i}&expand=description,owner,jql,searchUrl,viewUrl")
    .header('Content-Type', 'application/json')
    .header('Accept', 'application/json')
    .asObject(Map).body
    
    
    // Accessing the filters in the payload
    def filters = (List<Map<String, Map>>) response.values
    
    //if filters exist
    if(filters){
        // first, make the search keyword or phrase lower case, so it will be case insensitive
        def searchKeywordCaseInsensitive = "${SEARCH_KEYWORD}".toLowerCase();
        // go through each filter and see if the filter's jql contains the search keyword 
        filters.each{f -> 
             def jqlLowercase = f.jql.toString().toLowerCase()
             if(jqlLowercase.contains(searchKeywordCaseInsensitive)){
                 //if it does contain the search keyword, push it into the filterDataMatches array for storage to use later
                filterDataMatches << f
                return;
            } 
        }
    } else {
        return;
    }
}

// fix the formatting of the filterDataMatches array to be used for the CSV file
def fixedFilterFormat = (List<Map<String, Map>>) filterDataMatches

def numFiltersFound = fixedFilterFormat.size

//if any filters were found 
if(numFiltersFound !== 0) {
    try {
        //create a temp .csv file
        def exportFile = File.createTempFile("tmp", ".csv")
        exportFile.withPrintWriter { pw ->
            //create the headers
            pw.println('Id,Name,Description,Owner,ViewUrl, JQL Query')
            //map the data for each filter
            fixedFilterFormat.each { filter ->
                pw.println("${filter.id},${filter.name},${filter.description},${filter.owner.displayName}, ${filter.viewUrl}, ${filter.jql}")
            }
        }
        //post the .csv file created to the Jira issue
        def attachResult = post("/rest/api/3/issue/${issueKey}/attachments")
            .header('X-Atlassian-Token', 'no-check')
            .field("file", exportFile.newInputStream(), ContentType.create("text/csv"), "${SEARCH_KEYWORD}.csv")
            .asObject(List)
        
        //let the end user know the script is complete
        logger.info "\n -----> ${numFiltersFound} filters were found with jql containing the keyword or phrase, '${SEARCH_KEYWORD}', and has been attached to ${issueKey}."
    
    } finally {
        exportFile.delete()
    }
} else {
    //let the end user know that no filters with the search keyword were found
    logger.info "---> No filters with the keyword(s), ${SEARCH_KEYWORD} were found."
}