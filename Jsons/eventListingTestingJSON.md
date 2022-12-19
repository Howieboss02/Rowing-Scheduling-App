In order to test this API please add the following events:
```yaml
[  
    {  
        "owningUser": 1,  
        "label": "Bob's training",  
        "positions": [  
            {  
                "name": "Cox",  
                "competitive": true  
            },  
            {  
                "name": "Startboard",  
                "competitive": false  
            },  
            {  
                "name": "Startboard",  
                "competitive": false  
            }  
        ],  
        "startTime": "12:00",  
        "endTime": "14:00",  
        "certificate": "B1",  
        "type": "TRAINING",  
        "organisation": "Bob"  
    },  
    {  
        "owningUser": 2,  
        "label": "Bob's training1",  
        "positions": [  
            {  
                "name": "Cox",  
                "competitive": true  
            }  
        ],  
        "startTime": "12:00",  
        "endTime": "14:00",  
        "certificate": "B1",  
        "type": "TRAINING",  
        "organisation": "Bob"  
    },  
    {  
        "owningUser": 2,  
        "label": "Bob's training2",  
        "positions": [  
            {
                "name": "Cox",  
                "competitive": true  
            },  
            {  
                "name": "Coach",  
                "competitive": true  
            },  
            {  
                "name": "ScullingRower",  
                "competitive": false  
            }  
        ],  
        "startTime": "12:00",  
        "endTime": "14:00",  
        "certificate": "B3",  
        "type": "TRAINING",  
        "organisation": "Bob"  
    },  
    {  
        "owningUser": 3,  
        "label": "Best Competition",  
        "positions": [  
            {  
                "name": "Cox",  
                "competitive": true  
            },  
            {  
                "name": "Startboard",  
                "competitive": true  
            },  
            {  
                "name": "PortSideRower",  
                "competitive": true  
            }  
        ],  
        "startTime": "18:00",  
        "endTime": "20:00",  
        "certificate": "B4",  
        "type": "COMPETITION",  
        "organisation": "La Camp TUD"  
    },  
    {  
        "owningUser": 3,  
        "label": "Best Competition1",  
        "positions": [  
            {  
                "name": "Cox",  
                "competitive": true  
            },  
            {  
                "name": "Startboard",  
                "competitive": true  
            },  
            {  
                "name": "PortSideRower",  
                "competitive": true  
            }  
        ],  
        "startTime": "18:00",  
        "endTime": "20:00",  
        "certificate": "B3",  
        "type": "COMPETITION",  
        "organisation": "Bob"  
    }  
]  
```
Afterwarrds call http://localhost:8083/matchEvents sending this json:
```yaml
{  
    "id": 1,  
    "netId": " Bob1",  
    "name": "Bob Bobbie",  
    "organization": "Bob",  
    "email": "Bob@b.ob",  
    "gender": "Male",  
    "certificate": "B4",  
    "positions": [{"name": "Cox", "isCompetitive": "true"}, {"name": "Coach", "isCompetitive": "true"}, {"name": "Startboard", "isCompetitive": "false"}],  
    "notifications": []  
}
```
You should get this answer:
```yaml
[  
    {  
        "id": 2,  
        "owningUser": 2,  
        "label": "Bob's training1",  
        "positions": [  
            {  
                "name": "Cox",  
                "competitive": true  
            }  
        ],  
        "startTime": "12:00",  
        "endTime": "14:00",  
        "certificate": "B1",  
        "type": "TRAINING",  
        "organisation": "Bob"  
    },  
    {  
        "id": 3,  
        "owningUser": 2,  
        "label": "Bob's training2",  
        "positions": [  
            {  
                "name": "Cox",  
                "competitive": true  
            },  
            {  
                "name": "Coach",  
                "competitive": true  
            },  
            {  
                "name": "ScullingRower",  
                "competitive": false  
            }  
        ],  
        "startTime": "12:00",  
        "endTime": "14:00",  
        "certificate": "B3",  
        "type": "TRAINING",  
        "organisation": "Bob"  
    },  
    {  
        "id": 5,  
        "owningUser": 3,  
        "label": "Best Competition1",  
        "positions": [  
            {  
                "name": "Cox",  
                "competitive": true  
            },  
            {  
                "name": "Startboard",  
                "competitive": true  
            },  
            {  
                "name": "PortSideRower",  
                "competitive": true  
            }  
        ],  
        "startTime": "18:00",  
        "endTime": "20:00",  
        "certificate": "B3",  
        "type": "COMPETITION",  
        "organisation": "Bob"  
    }
]
```