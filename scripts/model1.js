// Stages that have been excluded from the aggregation pipeline query
__3tsoftwarelabs_disabled_aggregation_stages = [

	{
		// Stage 3 - excluded
		stage: 3,  source: {
			$match: {
			  _id:0
			}
		}
	},
]

db.stockdata.aggregate(

	// Pipeline
	[
		// Stage 1
		{
			$match: {
			    "group" : "A",
			    "date": ISODate("2017-08-20T18:30:00.000+0000") 
			}
		},

		// Stage 2
		{
			$match: {
			$or:[
			{"period": "weekly", "changetype":"+"},{"period": "monthly","changetype":"-"}
			]
			
			}
		},

		// Stage 4
		{
			$group: { 
			  _id : "$company", period: { $push: "$period" } , "count": { "$sum": 1 }, changetype: { $push: "$changetype" }, change : {$push: "$change"}
			  } 
		},

		// Stage 5
		{
			$match: {
			   count: {$gt: 1}
			}
		},

		// Stage 6
		{
			$sort: {
			  _id: 1
			}
		},

	]

	// Created with Studio 3T, the IDE for MongoDB - https://studio3t.com/

);
