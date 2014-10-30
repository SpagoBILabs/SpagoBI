/** SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. **/
 
  
 
 Ext.ns("Sbi.locale");
Sbi.locale.ln = Sbi.locale.ln || new Array();

Sbi.locale.formats = {
		
		float: {
			decimalSeparator: '.',
			decimalPrecision: 2,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		int: {
			decimalSeparator: '.',
			decimalPrecision: 0,
			groupingSeparator: ',',
			groupingSize: 3,
			//currencySymbol: '$',
			nullValue: ''
		},
		
		string: {
			trim: true,
    		maxLength: null,
    		ellipsis: true,
    		changeCase: null, // null | 'capitalize' | 'uppercase' | 'lowercase'
    		//prefix: '',
    		//suffix: '',
    		nullValue: ''
		},
		
		date: {
			dateFormat: 'm/Y/d',
    		nullValue: ''
		},
		
		boolean: {
			trueSymbol: 'true',
    		falseSymbol: 'false',
    		nullValue: ''
		}
};

//===================================================================
// MESSAGE WINDOW
//===================================================================

//===================================================================
//SEARCH FORM MESSAGE
//===================================================================
Sbi.locale.ln['sbi.social.analysis.search'] = 'Search';
Sbi.locale.ln['sbi.social.analysis.searchType'] = 'Search type';
Sbi.locale.ln['sbi.social.analysis.onlinemonitoring'] = 'On-line Monitoring';
Sbi.locale.ln['sbi.social.analysis.historicaldata'] = 'Historical Data';
Sbi.locale.ln['sbi.social.analysis.startingfrom'] = 'Starting from';
Sbi.locale.ln['sbi.social.analysis.dayago'] = 'day ago';
Sbi.locale.ln['sbi.social.analysis.repeatevery'] = 'Repeat every';
Sbi.locale.ln['sbi.social.analysis.hour'] = 'Hour/s';
Sbi.locale.ln['sbi.social.analysis.day'] = 'Day/s';
Sbi.locale.ln['sbi.social.analysis.week'] = 'Week/s';
Sbi.locale.ln['sbi.social.analysis.month'] = 'Month/s';
Sbi.locale.ln['sbi.social.analysis.logicalidentifier'] = 'Logical Identifier:';
Sbi.locale.ln['sbi.social.analysis.keywords'] = 'Keywords';
Sbi.locale.ln['sbi.social.analysis.keywordsfieldalertmessage'] = 'Please, use a comma as keywords separator. Max 5 keywords allowed';
Sbi.locale.ln['sbi.social.analysis.twitter'] = 'Twitter';
Sbi.locale.ln['sbi.social.analysis.facebook'] = 'Facebook';
Sbi.locale.ln['sbi.social.analysis.linkedin'] = 'Linkedin';
Sbi.locale.ln['sbi.social.analysis.accountstomonitor'] = 'Accounts to monitor:';
Sbi.locale.ln['sbi.social.analysis.accountstomonitorfieldalertmessage'] = 'Check input format. E.g. @Account1, @Account2, etc. Max 3 accounts allowed';
Sbi.locale.ln['sbi.social.analysis.resourcestomonitor'] = 'Resources to monitor:';
Sbi.locale.ln['sbi.social.analysis.resourcestomonitorfieldalertmessage'] = 'Check input format. E.g. http://bit.ly/yourbitly1, http://bit.ly/yourbitly2, etc. Max 3 bitly links allowed';
Sbi.locale.ln['sbi.social.analysis.documentstomonitor'] = 'Impact on business:';
Sbi.locale.ln['sbi.social.analysis.documentstomonitorfieldalertmessage'] = 'Please, use a comma as documents separator. Max 3 documents allowed';
Sbi.locale.ln['sbi.social.analysis.upto'] = 'Up to:';
Sbi.locale.ln['sbi.social.analysis.frequency'] = 'Frequency';
Sbi.locale.ln['sbi.social.analysis.timelyscanning'] = 'Timely Scanning';
Sbi.locale.ln['sbi.social.analysis.continuousscanning'] = 'Continuous Scanning';
Sbi.locale.ln['sbi.social.analysis.label'] = 'Label';
Sbi.locale.ln['sbi.social.analysis.lastactivation'] = 'Last Activation';
Sbi.locale.ln['sbi.social.analysis.documents'] = 'Documents';
Sbi.locale.ln['sbi.social.analysis.startstop'] = 'Start/Stop';
Sbi.locale.ln['sbi.social.analysis.stopstreammessage'] = 'Your are stopping the streaming search. Are you sure?';
Sbi.locale.ln['sbi.social.analysis.startstreammessage'] = 'Starting this Stream will stop an eventual other one active. Are you sure?';
Sbi.locale.ln['sbi.social.analysis.delete'] = 'Delete';
Sbi.locale.ln['sbi.social.analysis.deletingmessage'] = 'You are deleting this search. Are you sure?';
Sbi.locale.ln['sbi.social.analysis.analyse'] = 'Analyse';
Sbi.locale.ln['sbi.social.analysis.scheduler'] = 'Scheduler';
Sbi.locale.ln['sbi.social.analysis.schedulertooltip'] = 'Stop Historic Search Scheduler';
Sbi.locale.ln['sbi.social.analysis.stopsearchscheduler'] = 'You are stopping the search scheduler. Are you sure?';
Sbi.locale.ln['sbi.social.analysis.searchfailedmessage'] = ' - search failed cause of connection problems. Loading partial results..';
Sbi.locale.ln['sbi.social.analysis.and'] = 'AND';
Sbi.locale.ln['sbi.social.analysis.or'] = 'OR';
Sbi.locale.ln['sbi.social.analysis.free'] = 'Free';




//===================================================================
//TABS MESSAGE
//===================================================================
Sbi.locale.ln['sbi.social.analysis.summary'] = 'Summary';
Sbi.locale.ln['sbi.social.analysis.topics'] = 'Topics';
Sbi.locale.ln['sbi.social.analysis.network'] = 'Network' ;
Sbi.locale.ln['sbi.social.analysis.distribution'] = 'Distribution';
Sbi.locale.ln['sbi.social.analysis.sentiment'] = 'Sentiment';
Sbi.locale.ln['sbi.social.analysis.impact'] = 'Impact';
Sbi.locale.ln['sbi.social.analysis.roi'] = 'ROI';
Sbi.locale.ln['sbi.social.analysis.searchome'] = 'Home';

//SUMMARY
Sbi.locale.ln['sbi.social.analysis.users'] = 'users';
Sbi.locale.ln['sbi.social.analysis.searchrange'] = 'Search Range';
Sbi.locale.ln['sbi.social.analysis.timescale'] = 'Time Scale';
Sbi.locale.ln['sbi.social.analysis.hours'] = 'Hours';
Sbi.locale.ln['sbi.social.analysis.days'] = 'Days';
Sbi.locale.ln['sbi.social.analysis.weeks'] = 'Weeks';
Sbi.locale.ln['sbi.social.analysis.months'] = 'Months';
Sbi.locale.ln['sbi.social.analysis.tweetssummary'] = 'Tweets Summary';
Sbi.locale.ln['sbi.social.analysis.tweetssources'] = 'Tweets Sources';
Sbi.locale.ln['sbi.social.analysis.toptweets'] = 'Top Tweets';
Sbi.locale.ln['sbi.social.analysis.recenttweets'] = 'Recent Tweets';

//TOPICS
Sbi.locale.ln['sbi.social.analysis.hashtagscloud'] = 'Hashtags Cloud';
Sbi.locale.ln['sbi.social.analysis.topicscloud'] = 'Topics Cloud';

//NETWORK
Sbi.locale.ln['sbi.social.analysis.topinfluencers'] = 'Top Influencers';
Sbi.locale.ln['sbi.social.analysis.usersmentions'] = 'Users Mentions';
Sbi.locale.ln['sbi.social.analysis.usersinteractionsgraph'] = 'Users Interactions Graph';
Sbi.locale.ln['sbi.social.analysis.usersinteractionsmap'] = 'Users Interactions Map';

//DISTRIBUTION
Sbi.locale.ln['sbi.social.analysis.locationtweets'] = 'Location Tweets';

//SENTIMENT
Sbi.locale.ln['sbi.social.analysis.tweetspolarity'] = 'Tweets Polarity';
Sbi.locale.ln['sbi.social.analysis.sentimentradar'] = 'Sentiment Radar';
Sbi.locale.ln['sbi.social.analysis.positivestopics'] = 'Positives Topics';
Sbi.locale.ln['sbi.social.analysis.neutralstopics'] = 'Neutrals Topics';
Sbi.locale.ln['sbi.social.analysis.negativestopics'] = 'Negatives Topics';

//IMPACT
Sbi.locale.ln['sbi.social.analysis.accountsfollowerstimeline'] = 'Accounts Followers Timeline';
Sbi.locale.ln['sbi.social.analysis.bitlyclickstimeline'] = 'Bitly Clicks Timeline';