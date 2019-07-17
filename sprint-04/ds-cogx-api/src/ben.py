import datetime
import pandas as pd
import numpy as np
from scipy import sparse
from scipy.sparse import csr_matrix
from sklearn import metrics
from sklearn.ensemble import RandomForestClassifier
import copy
import time
import sqlite3
import traceback,sys
import json
from pandas.io.json import json_normalize

import constants
import hbase
import response


string_cols = ['KEY_CHK_DCN_NBR', 'PROC_CD_1', 'PROC_CD_2', 'PROC_CD_3', 'MBU_rollup', 'PROV_SPCLTY_CD', 'HCFA_PT_CD', 'LMT_CLS_A1_CD', 'LMT_CLS_A2_CD', 
               'LMT_CLS_A3_CD','LMT_CLS_B1_CD', 'LMT_CLS_B2_CD', 'LMT_CLS_B3_CD', 'LMT_CLS_C1_CD','LMT_CLS_C2_CD', 
               'LMT_CLS_C3_CD', 'LMT_CLS_D1_CD', 'LMT_CLS_D2_CD','LMT_CLS_D3_CD', 'LMT_CLS_E1_CD', 'LMT_CLS_E2_CD', 
               'LMT_CLS_E3_CD','MBR_CNTRCT_CD', 'TOS_TYPE_CD','PROC_SRVC_CLS_1_CD', 'PROC_SRVC_CLS_2_CD', 
               'PROC_SRVC_CLS_3_CD', 'ERR_1_CD','ERR_2_CD', 'ERR_3_CD', 'ERR_4_CD', 'ERR_5_CD', 
               'ERR_6_CD','ERR_7_CD', 'ERR_8_CD', 'ERR_9_CD', 'ERR_10_CD', 'ERR_11_CD','ERR_12_CD', 'ERR_13_CD', 'ERR_14_CD', 
               'ERR_15_CD', 'ERR_16_CD','ERR_17_CD', 'ERR_18_CD', 'ERR_19_CD', 'ERR_20_CD', 'ERR_21_CD','ERR_22_CD', 
               'ERR_23_CD', 'ERR_24_CD', 'ERR_25_CD', 'ERR_26_CD','ERR_27_CD', 'ERR_28_CD', 'ERR_29_CD', 'ERR_30_CD', 
               'ERR_31_CD','ERR_32_CD','PRVDR_STATUS','CLAIM_TYPE','PROD_NTWK','PROV_LCNS_CD','state_lob']
num_cols = ['DTL_LINE_NBR']
dt_cols = ['CLM_CMPLTN_DT', 'SRVC_FROM_DT_DTL', 'SRVC_TO_DT_DTL']
key = ['row']
header_dict = {'Revenue_CD': ['PROC_CD_1', 'PROC_CD_2', 'PROC_CD_3'],
               'LMT_CLS': ['LMT_CLS_A1_CD', 'LMT_CLS_A2_CD', 'LMT_CLS_A3_CD',
                           'LMT_CLS_B1_CD', 'LMT_CLS_B2_CD', 'LMT_CLS_B3_CD',
                           'LMT_CLS_C1_CD', 'LMT_CLS_C2_CD', 'LMT_CLS_C3_CD', 
                           'LMT_CLS_D1_CD', 'LMT_CLS_D2_CD', 'LMT_CLS_D3_CD', 
                           'LMT_CLS_E1_CD', 'LMT_CLS_E2_CD', 'LMT_CLS_E3_CD'],
               'MBU_rollup': ['MBU_rollup'],
               'state_lob': ['state_lob'],
               'PROV_SPCLTY_CD': ['PROV_SPCLTY_CD'],
               'HCFA_PT_CD': ['HCFA_PT_CD'], 
               'MBR_CNTRCT_CD': ['MBR_CNTRCT_CD'],
               'TOS_TYPE_CD': ['TOS_TYPE_CD'],
               'PROC_SRVC_CLS': ['PROC_SRVC_CLS_1_CD', 'PROC_SRVC_CLS_2_CD', 'PROC_SRVC_CLS_3_CD'],
               'ERR_CD': ['ERR_1_CD', 'ERR_2_CD', 'ERR_3_CD', 'ERR_4_CD', 'ERR_5_CD', 
                          'ERR_6_CD', 'ERR_7_CD', 'ERR_8_CD', 'ERR_9_CD', 'ERR_10_CD', 
                          'ERR_11_CD', 'ERR_12_CD', 'ERR_13_CD', 'ERR_14_CD', 'ERR_15_CD', 
                          'ERR_16_CD', 'ERR_17_CD', 'ERR_18_CD', 'ERR_19_CD', 'ERR_20_CD', 
                          'ERR_21_CD', 'ERR_22_CD', 'ERR_23_CD', 'ERR_24_CD', 'ERR_25_CD', 
                          'ERR_26_CD', 'ERR_27_CD', 'ERR_28_CD', 'ERR_29_CD', 'ERR_30_CD', 
                          'ERR_31_CD', 'ERR_32_CD'],
              'PRVDR_STATUS':['PRVDR_STATUS'],
              'CLAIM_TYPE':['CLAIM_TYPE'],
              'PROD_NTWK':['PROD_NTWK']}

header_num_cols = ['Emergency','Emergency_room','ASC_claim','proc_98']


def run(app, logger, extra, payload, payload_req):

    mbr_cntrct_cd_lst = []
    try :
        for clm_detail in payload :
            if clm_detail['MBR_CNTRCT_CD'] != None :
                mbr_cntrct_cd_lst.append(clm_detail['MBR_CNTRCT_CD']) 
        mbr_cntrct_cd_set = list(set(mbr_cntrct_cd_lst))
        mbr_cntrct_cd = "('" + "','".join(mbr_cntrct_cd_set) + "')"
        #mbr_cntrct_cd = str(payload[0]['MBR_CNTRCT_CD'])
        start_time = time.time()
        result_row = {}
        try:            
            output = json.loads(hbase.hbaseLookup(mbr_cntrct_cd_set[0], constants.HBASE_BEN_COLUMN, logger, extra))
            ben_reference_data = pd.DataFrame(output['cogxbenefit'])
            ben_reference_data.rename(columns={'NOTES':'NOTE'}, inplace=True)
            '''
            conn = sqlite3.connect(constants.DB_PATH_BEN)
            query = 'SELECT * FROM ben WHERE CONTRACT in '+mbr_cntrct_cd
            #ben_reference_data = pd.read_sql_query('SELECT * FROM ben WHERE CONTRACT in  ?', conn, params=(mbr_cntrct_cd, ))
            ben_reference_data = pd.read_sql_query(query, conn)
            ben_reference_data['START_DT'] = ben_reference_data['START_DT'].apply(lambda x : x[6:] + '-' + x[0:2] + '-' + x[3:5])
            ben_reference_data['END_DT'] = ben_reference_data['END_DT'].apply(lambda x : x[6:] + '-' + x[0:2] + '-' + x[3:5])
            '''
        except Exception as e:
            logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=extra)
            return response.generate_desc(711, 'ben', result_row, payload_req)

        logger.debug('Elapsed time', extra=response.extra_tags(extra, {'Reference Data Fetch': time.time() - start_time}, 0))
        
        if len(ben_reference_data) == 0:
            return response.generate_desc(711, 'ben', result_row, payload_req)

        logger.debug('Number of ben rows found %d' % len(ben_reference_data), extra=extra)
        raw_X = pd.DataFrame.from_dict(payload, orient='columns')
        raw_X = raw_X.rename(columns={'PROC_CD':'PROC_CD_1','HCPCS_CD':'PROC_CD_2','MEDCR_PROC_CD':'PROC_CD_3'})
        raw_X.FULL_MNGMNT_BU_CD = raw_X.FULL_MNGMNT_BU_CD.apply(str)
        MBU = app.config['data_frames']['MBU'] 
        col_dict = copy.deepcopy(app.config['data_frames']['ben_col_list'])
        # search = MBU.loc[MBU['FULL_MNGMNT_BU_CD'] == raw_X.FULL_MNGMNT_BU_CD[0]]
        search = MBU.ix[raw_X.FULL_MNGMNT_BU_CD[0]]
        # raw_X = raw_X.merge(MBU, how='left',on='FULL_MNGMNT_BU_CD')
        raw_X['MBU_rollup'] = search['MBU_rollup']
        raw_X['state_lob'] = search['state_lob']
        raw_X = clean_raw_data(raw_X, string_cols, dt_cols, num_cols)   
        # Emergency
        raw_X['Emergency'] = np.array(((raw_X.PROC_CD_1 == '0450') | (raw_X.PROC_CD_2 == '0450') | (raw_X.PROC_CD_3 == '0450')) | ((raw_X.HCFA_PT_CD == '23') & (raw_X.CLAIM_TYPE == 'OUTPT'))) * 1
        col_list = ['PROC_SRVC_CLS_2_CD', 'PROC_SRVC_CLS_3_CD']
        # Emergency_room
        Emergency_room = raw_X.PROC_SRVC_CLS_1_CD.isin(['05A', '521'])
        for col_item in col_list:
            Emergency_room = Emergency_room | raw_X[col_item].isin(['05A', '521'])
        raw_X['Emergency_room'] = np.array(Emergency_room) * 1
        # ASC_claim
        ASC_claim = (raw_X.HCFA_PT_CD == '24') | (raw_X.PROV_LCNS_CD.str.startswith('06'))
        raw_X['ASC_claim'] = np.array(ASC_claim) * 1

        raw_X_agg = raw_X.groupby(['KEY_CHK_DCN_NBR', 'CLM_CMPLTN_DT'], as_index=False)['Emergency','Emergency_room','ASC_claim'].max()
        raw_X = raw_X.drop(columns=['Emergency','Emergency_room','ASC_claim'])
        raw_X = raw_X.merge(raw_X_agg,how='left', on=['KEY_CHK_DCN_NBR', 'CLM_CMPLTN_DT'])
        raw_X = raw_X.drop_duplicates(subset=['KEY_CHK_DCN_NBR','DTL_LINE_NBR','TOS_TYPE_CD']).reset_index(drop=True)
        raw_X['MDFR_CD_drop'] = '*'
        raw_X['Procedure_Code'] = ''
        raw_X['HCPCS_Code'] = ''
        raw_X['Revenue_code'] = ''
        for cols in ['PROC_CD_1', 'PROC_CD_2', 'PROC_CD_3']:
            raw_X.loc[(raw_X[cols] != raw_X['MDFR_CD_drop']) & (raw_X[cols].str.len()==5) & (~raw_X[cols].str[0].str.isnumeric()),'HCPCS_Code'] = raw_X.loc[(raw_X[cols] != raw_X['MDFR_CD_drop']) & (raw_X[cols].str.len()==5) & (~raw_X[cols].str[0].str.isnumeric()),cols]
            raw_X.loc[(raw_X[cols] != raw_X['MDFR_CD_drop']) & (raw_X[cols].str.len()==4) & (raw_X[cols].str.isnumeric())& (raw_X[cols].str[0] == '0'),'Revenue_code'] = raw_X.loc[(raw_X[cols] != raw_X['MDFR_CD_drop']) & (raw_X[cols].str.len()==4) & (raw_X[cols].str.isnumeric())& (raw_X[cols].str[0] == '0'),cols]
            raw_X.loc[(raw_X[cols] != raw_X['MDFR_CD_drop']) & (raw_X[cols] != raw_X['HCPCS_Code']) & (raw_X[cols] != raw_X['Revenue_code']) & (raw_X['Procedure_Code'] == ''),'Procedure_Code'] = raw_X.loc[(raw_X[cols] != raw_X['MDFR_CD_drop']) & (raw_X[cols] != raw_X['HCPCS_Code']) & (raw_X[cols] != raw_X['Revenue_code']) & (raw_X['Procedure_Code'] == ''),cols]        
        raw_X['proc_98'] = np.array(raw_X['Procedure_Code'].str.startswith('98')) * 1
        raw_X = raw_X.drop(columns=['MDFR_CD_drop','Procedure_Code','HCPCS_Code','Revenue_code'])
        raw_X['row'] = raw_X.index
        row_dict = raw_X.loc[:,['KEY_CHK_DCN_NBR', 'CURNT_QUE_UNIT_NBR', 'DTL_LINE_NBR','row']]
        X_data = sparse.csr_matrix(np.empty([raw_X.shape[0],0]))
        col_dict2 = pd.DataFrame(columns=['col','VAR'])
        col_index = 0
        
        '''
        for dict_key, dict_value in header_dict.items():
            temp_df = combine_col(raw_X, key, dict_value, dict_key, col_dict, dict_key, True)  ### Threshold 
            col_dict2 = pd.concat([col_dict2, pd.DataFrame({'col':range(col_index, col_index+temp_df.shape[1]),'VAR':temp_df.columns.values})])
            col_index = col_index+temp_df.shape[1]
            temp_df['row'] = temp_df.index
            temp_df = temp_df.reset_index(drop=True)
            X_data = sparse.hstack((X_data, sparse.csr_matrix(row_dict.loc[:,['row']].merge(temp_df, how='left').drop(columns=['row']).fillna(0).values)))+0.0
        header_num= raw_X[['row']+header_num_cols]
        header_num.head()
        X_data = sparse.hstack((X_data, sparse.csr_matrix(row_dict.loc[:,['row']].merge(header_num, how='left').drop(columns=['row']).fillna(0).values)))+0.0 
        X_data = X_data.tocsr()
        X_data = X_data[:, (~np.array(col_dict['VAR'].str.startswith("Revenue_CD_")))]
        '''
        X_data = combine_col_2(raw_X, col_dict, header_dict, header_num_cols)
        
        X_data = sparse.csr_matrix(X_data.values)
        last = X_data.shape[1]
        temp = np.array(col_dict['VAR'].str.startswith("Revenue_CD_"))
        temp = ~np.array(col_dict['VAR'].str.startswith("Revenue_CD_"))
        temp= temp[0:last]
        X_data = X_data[:, temp]

        m = X_data.shape[0]
        X_2 = X_data[np.array(range(0,m)),:]
        '''
        arry = X_2.toarray()
        for arry1 in arry:
            print('################################')
            for idx, val in enumerate(arry1):
                if val != 0 and np.isnan(val) == False:
                    print('(0,' + str(idx) + ')\t' + str(val))
            print('*******************************')
        '''
        usecases = app.config['usecases']
        models = []
        test_row_1 = {}
        for usecase in usecases:
            if (usecase['name'] == 'ben'):
                models = usecase['models']
                break

        for model in models:
            # There is only one model
            rf = copy.deepcopy(model['binary'])            
        
        pred_train = rf.predict_proba(X_2)
        df_train = pd.DataFrame(pred_train, columns=rf.classes_)    
        df_train = pd.concat([row_dict.rename(columns={'KEY_CHK_DCN_NBR': 'DCN','DTL_LINE_NBR': 'Line#'}),df_train],axis=1).reset_index(drop=True).sort_values(by=['DCN','Line#']).reset_index(drop=True)

        raw_X_sub = raw_X.loc[raw_X.KEY_CHK_DCN_NBR.isin(df_train.DCN),['KEY_CHK_DCN_NBR', 'DTL_LINE_NBR','MBR_CNTRCT_CD', 'SRVC_FROM_DT_DTL', 'SRVC_TO_DT_DTL']].reset_index(drop=True)

        benefits_reference_sub = ben_reference_data.loc[:,['CONTRACT','START_DT','END_DT','MAJOR_HEADING','MINOR_HEADING','VARIABLE_FORMAT','VARIABLE_DESC','VARIABLE_NETWORK','VARIABLE_VALUE','NOTE','ACCUM_CODE']].rename(columns={'CONTRACT':'MBR_CNTRCT_CD'}).reset_index(drop=True)
        benefits_reference_sub = benefits_reference_sub.drop_duplicates()
        benefits_reference_sub.MBR_CNTRCT_CD = benefits_reference_sub.MBR_CNTRCT_CD.apply(str)
        # benefits_reference_sub['MINOR_HEADING'] = benefits_reference_sub['MINOR_HEADING'].apply(lambda x: x.strip().upper())
        # benefits_reference_sub['VARIABLE_DESC'] = benefits_reference_sub['VARIABLE_DESC'].apply(lambda x: x.strip().upper())
        # benefits_reference_sub['VARIABLE_FORMAT'] = benefits_reference_sub['VARIABLE_FORMAT'].apply(lambda x: x.strip().upper())
        
        benefits_reference_sub['MINOR_HEADING'] = benefits_reference_sub['MINOR_HEADING'].str.strip()
        benefits_reference_sub['MINOR_HEADING'] = benefits_reference_sub['MINOR_HEADING'].str.upper()
        benefits_reference_sub['VARIABLE_DESC'] = benefits_reference_sub['VARIABLE_DESC'].str.strip()
        benefits_reference_sub['VARIABLE_DESC'] = benefits_reference_sub['VARIABLE_DESC'].str.upper()
        benefits_reference_sub['VARIABLE_FORMAT'] = benefits_reference_sub['VARIABLE_FORMAT'].str.strip()
        benefits_reference_sub['VARIABLE_FORMAT'] = benefits_reference_sub['VARIABLE_FORMAT'].str.upper()
        
        benefits_reference_sub.loc[pd.isnull(benefits_reference_sub.NOTE),'NOTE'] = ''
        benefits_reference_sub.loc[pd.isnull(benefits_reference_sub.ACCUM_CODE),'ACCUM_CODE'] = ''
        benefits_reference_sub.loc[benefits_reference_sub['MINOR_HEADING'] == "INPATIENT HOSPITAL-MEDICAL",'MINOR_HEADING'] = "INPATIENT HOSPITAL - MEDICAL"
        benefits_reference_sub['reference_target'] = benefits_reference_sub['MINOR_HEADING'] + '|' + benefits_reference_sub['VARIABLE_DESC']
        
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT.isin(['AGE','HRS','MIN','SES','TBL','VST','Y/N','YRS']),'VARIABLE_VALUE'] = benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT.isin(['AGE','HRS','MIN','SES','TBL','VST','Y/N','YRS']),'VARIABLE_VALUE'] + ' ' + benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT.isin(['AGE','HRS','MIN','SES','TBL','VST','Y/N','YRS']),'VARIABLE_FORMAT']
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT.isin(['DAY','LEN']),'VARIABLE_VALUE'] = benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT.isin(['DAY','LEN']),'VARIABLE_VALUE'] + ' DAYS'
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'DOL','VARIABLE_VALUE'] = benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'DOL','VARIABLE_VALUE'] + ' $'
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'MIL','VARIABLE_VALUE'] = benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'MIL','VARIABLE_VALUE'] + ' MILE'
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'MTH','VARIABLE_VALUE'] = benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'MTH','VARIABLE_VALUE'] + ' MTHS'
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'NUM','VARIABLE_VALUE'] = benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'NUM','VARIABLE_VALUE'] + ' NBR'
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'OCC','VARIABLE_VALUE'] = benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'OCC','VARIABLE_VALUE'] + ' OCRS'
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'PCT','VARIABLE_VALUE'] = benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT == 'PCT','VARIABLE_VALUE'] + ' %'
        
        
        benefits_reference_sub['BSIBA'] = "BSI"
        benefits_reference_sub.loc[benefits_reference_sub.VARIABLE_FORMAT.isin(['ACC','AGE','IEB','MIN','TBL','VAL','Y/N']),'BSIBA'] = "BA"
        
        raw_X_sub = raw_X_sub.merge(benefits_reference_sub, how='inner', on=['MBR_CNTRCT_CD'])
        raw_X_sub.loc[raw_X_sub['END_DT'] == '9999-12-31','END_DT'] = '2049-12-31'
        raw_X_sub['START_DT'] = pd.to_datetime(raw_X_sub['START_DT'], format='%Y-%m-%d') #format='%d-%b-%y'
        raw_X_sub['END_DT'] = pd.to_datetime(raw_X_sub['END_DT'], format='%Y-%m-%d') #format='%d-%b-%y'

        raw_X_sub['SRVC_TO_DT_DTL'] = pd.to_datetime(raw_X_sub['SRVC_TO_DT_DTL'], format='%m/%d/%Y') #format='%d-%b-%y'
        raw_X_sub['SRVC_FROM_DT_DTL'] = pd.to_datetime(raw_X_sub['SRVC_FROM_DT_DTL'], format='%m/%d/%Y') #format='%d-%b-%y'

        raw_X_sub = raw_X_sub.loc[(raw_X_sub.SRVC_TO_DT_DTL <= raw_X_sub.END_DT) & (raw_X_sub.SRVC_FROM_DT_DTL >= raw_X_sub.START_DT ),:].reset_index(drop=True)
        raw_X_sub['BSI VARIABLE_DESC'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','BSI VARIABLE_DESC'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','VARIABLE_DESC']
        raw_X_sub['BSI VARIABLE_NETWORK'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','BSI VARIABLE_NETWORK'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','VARIABLE_NETWORK']
        raw_X_sub['BSI VARIABLE_VALUE'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','BSI VARIABLE_VALUE'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','VARIABLE_VALUE']
        raw_X_sub['BSI NOTE'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','BSI NOTE'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','NOTE']
        raw_X_sub['BSI ACCUM_CODE'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','BSI ACCUM_CODE'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BSI','ACCUM_CODE']
        raw_X_sub['BA VARIABLE_DESC'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','BA VARIABLE_DESC'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','VARIABLE_DESC']
        raw_X_sub['BA VARIABLE_NETWORK'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','BA VARIABLE_NETWORK'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','VARIABLE_NETWORK']
        raw_X_sub['BA VARIABLE_VALUE'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','BA VARIABLE_VALUE'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','VARIABLE_VALUE']
        raw_X_sub['BA NOTE'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','BA NOTE'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','NOTE']
        raw_X_sub['BA ACCUM_CODE'] = ''
        raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','BA ACCUM_CODE'] = raw_X_sub.loc[raw_X_sub.BSIBA == 'BA','ACCUM_CODE']

        def subset_top1_filter(x, df_train,raw_X_sub,col_list):
            df_train_sub = df_train.loc[(df_train.row == x),:].reset_index(drop=True)
            raw_X_sub_sub = raw_X_sub.loc[(raw_X_sub.KEY_CHK_DCN_NBR == df_train_sub.DCN[0]) & (raw_X_sub.DTL_LINE_NBR == df_train_sub['Line#'][0]),:].reset_index(drop=True)
            if (len(raw_X_sub_sub) == 0) | (len(raw_X_sub_sub.drop_duplicates(subset=['MBR_CNTRCT_CD','START_DT','END_DT']))>1):
                return df_train.loc[:,col_list].idxmax(axis=1)[0],0,'','','','','','','','','',''

            col_list = col_list[np.isin(col_list,raw_X_sub_sub.reference_target)]
            if (len(col_list) == 0):
                return 'NAN|NAN',0,'','','','','','','','','',''
            df_train_sub = df_train_sub.loc[:,col_list]
            model_predict = df_train_sub.loc[:,col_list].idxmax(axis=1)[0]
            raw_X_sub_sub_sub = raw_X_sub_sub.loc[raw_X_sub_sub.reference_target == model_predict,:].reset_index(drop=True)
            return model_predict,df_train_sub.loc[:,col_list].max(axis=1)[0],'|'.join(raw_X_sub_sub_sub['BSI VARIABLE_DESC'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BSI VARIABLE_NETWORK'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BSI VARIABLE_VALUE'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BSI NOTE'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BSI ACCUM_CODE'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BA VARIABLE_DESC'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BA VARIABLE_NETWORK'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BA VARIABLE_VALUE'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BA NOTE'].drop_duplicates()),'|'.join(raw_X_sub_sub_sub['BA ACCUM_CODE'].drop_duplicates())

           
        def subset_top1_filter2(x, df_train,raw_X_sub,col_list):
            df_train_sub = df_train.loc[(df_train.row == x),:].reset_index(drop=True)
            raw_X_sub_sub = raw_X_sub.loc[(raw_X_sub.KEY_CHK_DCN_NBR == df_train_sub.DCN[0]) & (raw_X_sub.DTL_LINE_NBR == df_train_sub['Line#'][0]),:].reset_index(drop=True)
            if (len(raw_X_sub_sub) == 0) | (len(raw_X_sub_sub.drop_duplicates(subset=['MBR_CNTRCT_CD','START_DT','END_DT']))>1):
                return ''
            return raw_X_sub_sub.loc[:,['MINOR_HEADING','BSI VARIABLE_DESC','BSI VARIABLE_NETWORK','BSI VARIABLE_VALUE','BSI NOTE','BSI ACCUM_CODE','BA VARIABLE_DESC','BA VARIABLE_NETWORK','BA VARIABLE_VALUE','BA NOTE','BA ACCUM_CODE']].reset_index(drop=True)

        t1 = time.time()
        df_train['top1'],df_train['max_score'],df_train['BSI Variable Desc'],df_train['BSI Variable Network'],df_train['BSI Variable Value'],df_train['BSI Note'],df_train['BSI ACCUM_CODE'],df_train['BA Variable Desc'],df_train['BA Variable Network'],df_train['BA Variable Value'],df_train['BA Note'],df_train['BA ACCUM_CODE']= zip(*df_train['row'].apply(subset_top1_filter,args=(df_train,raw_X_sub,rf.classes_,)))        
        logger.debug('Elapsed time',extra=response.extra_tags(extra, {'subset_top1_filter': time.time() - t1}, 0))
        t1 = time.time()
        df_train['All Possible Benefit'] = df_train['row'].apply(subset_top1_filter2,args=(df_train,raw_X_sub,rf.classes_,))
        # df_train['All Possible Benefit'] = ''
        logger.debug('Elapsed time',extra=response.extra_tags(extra, {'subset_top1_filter2': time.time() - t1}, 0))
        df_train['Benefit Minor Recommendation'],df_train['Benefit Variable (Desc) Recommendation'] = df_train.top1.str.split('|').str
        df_predict_top3 = df_train.loc[:,['DCN', 'CURNT_QUE_UNIT_NBR', 'Line#', 'row','top1', 'max_score', 'Benefit Minor Recommendation','Benefit Variable (Desc) Recommendation', 'BSI Variable Desc', 'BSI Variable Network','BSI Variable Value','BSI Note', 'BA Variable Desc', 'BA Variable Network','BA Variable Value','BA Note', 'All Possible Benefit']].rename(columns={'top1':'Benefits'})
        df_claim = df_predict_top3.groupby(by='DCN', as_index=False).agg({'max_score':'min','CURNT_QUE_UNIT_NBR':'first','Benefits':'first'})
        df_claim = df_claim.sort_values(by=['max_score'], ascending=False).reset_index(drop=True)
        
        #df_claim = df_claim.loc[df_claim.max_score >= constants.BEN_THRESHOLD,:].reset_index(drop=True)


        df_claim1 = df_claim.loc[df_claim.max_score >= constants.BEN_THRESHOLD,:].reset_index(drop=True)
        df_predict_top3['Service Code'] = raw_X['TOS_TYPE_CD']
        df_predict_top3['Place of Service'] = raw_X['HCFA_PT_CD']


        df_predict = df_predict_top3.loc[:,['DCN','Line#','Service Code','Place of Service','Benefit Minor Recommendation','Benefit Variable (Desc) Recommendation', 'BSI Variable Desc', 'BSI Variable Network','BSI Variable Value','BSI Note', 'BA Variable Desc', 'BA Variable Network','BA Variable Value','BA Note', 'All Possible Benefit']]
        df_claim['Benefit Details'] = df_claim['DCN'].apply(subset_detail, args=(df_predict,['DCN','Line#','Service Code','Place of Service', 'Benefit Minor Recommendation','Benefit Variable (Desc) Recommendation', 'BSI Variable Desc', 'BSI Variable Network','BSI Variable Value','BSI Note', 'BA Variable Desc', 'BA Variable Network','BA Variable Value','BA Note', 'All Possible Benefit'],))
        df_claim = df_claim.loc[:,['DCN','CURNT_QUE_UNIT_NBR','Benefit Details']]

        #df_claim = df_claim.loc[df_claim.max_score >= constants.BEN_THRESHOLD,:].reset_index(drop=True)

        if df_claim1.empty:
            logger.debug('Elapsed time',extra=response.extra_tags(extra, {'result_row building': time.time() - start_time}, 0))
            return response.generate_desc(710,"ben",result_row, payload_req)

        df_predict_top3 = df_predict_top3.merge(df_claim.loc[:,['DCN','topbottom']],how='inner',on=['DCN'])
        df_predict_top3 = df_predict_top3.fillna('')
        final_out = df_predict_top3.loc[:, ['DCN','Line#','CURNT_QUE_UNIT_NBR','Benefits','max_score']]
        recommendations = []
        for idx in range(final_out.shape[0]):
            recommendation = {}
            recommendation['modelName'] = 'BEN'
            recommendation['description'] = None
            recommendation['lineNumber'] = str(int(final_out['Line#'].iloc[idx]))
            if len(recommendation['lineNumber']) == 1:
                recommendation['lineNumber'] = '0' + recommendation['lineNumber']
            recommendation['actionCode'] = 'BEN'
            recommendation['actionValue'] = final_out['Benefits'].iloc[idx]
            # limiting number of characters based on mainframe restriction
            if len(recommendation['actionValue']) > constants.ACTION_VALUE_LENGTH:
                recommendation['actionValue'] = recommendation['actionValue'][0:constants.ACTION_VALUE_LENGTH]
            recommendation['modelScore'] = float(final_out['max_score'].iloc[idx])
            recommendation['currentQueue'] = payload_req.get('CURNT_QUE_UNIT_NBR', None)
            recommendations.append(recommendation)
        result_row['recommendations'] = recommendations

        model_insights = []
        model_insight = {}
        model_insight['modelName'] = 'ben'
        model_insight['modelInsight'] =  df_claim.to_json()
        model_insights.append(model_insight)
        
        result_row['modelInsights'] = model_insights

        df_predict_top1 = df_predict_top3.loc[:,['DCN','Line#','Service Code','Place of Service','Benefit summary','Under Benefit Summary']]
        df_predict = df_predict_top1
        df_predict['Benefit Major'] = df_predict['Benefit summary']
        df_predict['Benefit Minor'] = df_predict['Under Benefit Summary']
        response.generate_desc(700, 'ben', result_row, payload_req)
        return result_row
    except:
        logger.error((''.join(map(str.rstrip, traceback.format_exception(*sys.exc_info())))).replace('\n', ' '),extra=extra)
        return response.generate_desc(903, 'ben',result_row, payload_req)



def clean_raw_data(raw_data, string_cols, dt_cols, num_cols):
    for col in string_cols:
        raw_data[col] = raw_data[col].apply(str)
    for col in dt_cols:
        raw_data[col] = raw_data[col].apply(str)
        # raw_data[col][raw_data[col] == '12/31/9999'] = '12/31/2049'
        # raw_data[col][raw_data[col] == '9999-12-31'] = '2049-12-31'
        if '/' in raw_data[col][0]:
            raw_data[col] = raw_data[col].apply(lambda x: '12/31/2049' if x == '12/31/9999' else x)
            raw_data[col] = pd.to_datetime(raw_data[col], format='%m/%d/%Y')
        elif '-' in raw_data[col][0]:
            raw_data[col] = raw_data[col].apply(lambda x: '2049-12-31' if x == '9999-12-31' else x)
            raw_data[col] = pd.to_datetime(raw_data[col], format='%Y-%m-%d')
    for col in num_cols:
        raw_data[col] = pd.to_numeric(raw_data[col], downcast='float',errors='coerce')
    return raw_data


def combine_col_2(df, col_dict, header_dict, header_num_cols):
    # pd.DataFrame(data=np.array([[0]*len(col_dict)]*df.shape[0]), columns=col_dict['VAR'])
    new_df = None
    values = []
    for i in range(df.shape[0]):
        col_dict['value'] = np.nan
        value = []
        for dict_key, dict_value in header_dict.items():
            for val in dict_value:
                value.append(dict_key + '_' + str(df[val][i]))

        sub_set = col_dict.loc[col_dict['VAR'].isin(value)]
        col_dict.loc[sub_set.index, 'value'] = 1
        vals = col_dict['value'].tolist()
        # Done with str col types now adding num col types
        sub_set = col_dict.loc[col_dict['VAR'].isin(header_num_cols)]
        for idx in sub_set.index:
            vals[int(sub_set['col'][idx])] = df[sub_set['VAR'][idx]][i]
        
        values.append(vals)
        
    # columns = col_dict['VAR'].tolist().extend(header_num_cols)

    new_df = pd.DataFrame(data=np.array(values), columns=col_dict['VAR'])
        
    return new_df


def combine_col(df, key, cols, prefix, col_dict, dict_key, remove_star_code=False):
    index_col = key
    cat_cols = cols
    temp_df = df[index_col + [cat_cols[0]]].drop_duplicates().rename(columns={cat_cols[0]: "VAR"})
    for i in range(len(cols)):
        if i > 0:
            cat = cat_cols[i]
            temp_df = pd.concat([temp_df, df[index_col + [cat]].rename(columns={cat: "VAR"}).drop_duplicates()])
    temp_df = temp_df.drop_duplicates()
    temp_df['VAR'] = temp_df['VAR'].str.strip()
    if remove_star_code:
        temp_df = temp_df[temp_df['VAR'] != '*']
    num_claims = df.shape[0]
    temp_df['VAR'] = prefix + '_' + temp_df['VAR']
    temp_df['value'] = 1
    temp_df_dummy = temp_df.pivot(index='row',columns='VAR',values='value')
    temp_df_dummy = temp_df_dummy.reindex(columns=col_dict.VAR[col_dict.VAR.str.startswith(dict_key)].values)
    return temp_df_dummy


def subset_detail(x, df_detail,detail_compare_cols):
    df_detail_sub = df_detail.loc[(df_detail.DCN == x),detail_compare_cols].sort_values(['Line#'])
    return df_detail_sub.loc[:,detail_compare_cols]
