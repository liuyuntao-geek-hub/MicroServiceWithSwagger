#!/usr/bin/ksh
# Program:      coca_srvcacct_regen_kerb_tckt.sh
# Date:         11/30/2016
# Author:       Anthem
# Purpose:      To renew Kerberos ticket as it expires every 10 hours
# Location:     /app/eph/ccca/phi/no_gbd/bin/<env>/scripts/shell/
#
# Exampleusage: /ts/app/vs2/pdp/pndo/phi/no_gbd/r000/bin/scripts/cogx_api_kerberos.sh

kinit srcccpcogxapits@DEVAD.WELLPOINT.COM -kt /home/srcccpcogxapits/srcccpcogxapits.keytab