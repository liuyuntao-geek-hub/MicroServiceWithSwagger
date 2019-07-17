import traceback,sys
import copy


def get_collections(client, db_name):
    try:
        return client.db_name.list_collection_names()
    except:
        print("ERROR IN GETTING DATABASES LIST : can be privilage issue")
        return None


def insert_one(client, value, db_name='cognativedb', collection='processed_claims', logger=None):
    '''insert value object as one record in mongo and return object_id or None in case of faliure'''
    try:
        if isinstance(value, dict):
            db = client.get_database(db_name)
            coll = db.get_collection(collection)
            value = copy.deepcopy(value)

            obj_id = coll.insert_one(value).inserted_id
            print(obj_id)
            #print(recommendations_compare(client,recommendation_dict=value))
            return obj_id
            
        else:
            return None
    except Exception as e:
        if logger:
            logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
        else:
            print((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
    return None

def update_one(client,db_name = 'cognativedb',collection_name = 'processed_claims',key= ('KEY_CHK_DCN_NBR','KEY_CHK_DCN_ITEM_CD'),data = {},logger=None):
    try:
        db = client.get_database(db_name)
        coll = db.get_collection(collection_name)
        count = coll.update_one(key,data,upsert=True).modified_count
        return count
    except:
        if logger:
            logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
        else:
            print((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
    return None

def fetch_results(client,db_name='cognativedb',collection_name='processed_claims',params= {},maxTimeMS = 200,limit= 1,logger = None):
    '''return list of match result using find command'''
    try:
        db = client.get_database(db_name)
        coll = db.get_collection(collection_name)
        if limit == 1:
            return [coll.find_one(params,{'_id':0})]
        return list(coll.find(params,{'_id':0}).max_await_time_ms(maxTimeMS).limit(limit))
    except:
        if logger:
            logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
        else:
            print((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
    return None

def json_compare(recom_dict,store_data_item):
    return ordered(recom_dict) == ordered(store_data_item)


def ordered(obj):
    ''' return object in sorted order'''
    if isinstance(obj, dict):
        return sorted((k, ordered(v)) for k, v in obj.items())
    if isinstance(obj, list):
        return sorted(ordered(x) for x in obj)
    else:
        return obj

def recommendations_compare(client,db_name='cognativedb',collection_name='processed_claims',recommendation_dict = None,cmp_params = ('KEY_CHK_DCN_NBR','KEY_CHK_DCN_ITEM_CD')):
    '''return False in case of not match otherwise True (match or not found)'''
    params = {}
    for item in cmp_params:
        params[item] = recommendation_dict[item]
    cmp_result = False
    store_data = fetch_results(client,params = params)
    if len(store_data) == 0:
        return True
    for data in store_data:
        cmp_result = True
        if json_compare(copy.deepcopy(recommendation_dict),copy.deepcopy(data)) == False:
            return False
    return  cmp_result


def insert_upsert(client, db_name='cognativedb', collection='processed_claims', value={}, upsert=False,
               key=('KEY_CHK_DCN_NBR', 'KEY_CHK_DCN_ITEM_CD'), logger=None):
    '''insert or update if exist'''
    try:
        if isinstance(value, dict):
            db = client.get_database(db_name)
            coll = db.get_collection(collection)
            value = copy.deepcopy(value)
            # TODO : add object_id in logs in controller.py and also check if object_id is none
            if upsert == True:
                if isinstance(key, dict):
                    obj_id = coll.update(key, value).modified_count
                elif isinstance(key, tuple):
                    param_key = {}
                    for item in key:
                        param_key[item] = value.get(item, None)
                    obj_id = coll.update(param_key, value).modified_count
                print("modified count : "+str(obj_id))
            else:
                obj_id = coll.insert_one(value).inserted_id
                print("INSERT WITH OBJECT ID " + str(obj_id))
            return obj_id

        else:
            return None
    except Exception as e:
        if logger:
            logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
        else:
            print((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '))
    return None


#TESTING :
if __name__ == '__main__':
    uri = 'mongodb://127.0.0.1:27017'
    client= None
    from pymongo import MongoClient
    from pymongo.errors import ConfigurationError,ConnectionFailure
    import ssl
    try:
        client = MongoClient(uri,ssl=False)
    except ConfigurationError as e:
        print("CONFIG ERROR")
    except ConnectionFailure as e:
        print("Server not available")
    print(fetch_results(client,params= {'name':'Audi'}))

    # TODO : insert_one need upsert support to update the record if existing?
    # TODO : add object_id in logs in controller.py and also check if object_id is none
    # TODO : db.ensureIdex({'KEY_CHK_DCN_NBR':1,'KEY_CHK_DCN_ITEM_CD':1},{'unique':True})
    # TODO : steps to have properties configured as per env for databases, logs and other
    ## TODO : make connection unbounded or set maxPoolSize = 200
    # TODO : test decimal values in mongodb while we save
    ## TODO : never pass global parent mongo client to child processes
    '''
    db.createCollection( "processed_claims",
   { validator: { $or:
      [
         { KEY_CHK_DCN_NBR: { $type: "string" } },
         { email: { $regex: /@anthem\.com$/ } },
         { PROCESS: { $in: [ "Unknown", "Incomplete" ] } }
      ]
   }
} )
    '''