#!/usr/bin/ksh
# Program:      hpip_srvcacct_regen_kerb_tckt.sh
# Date:         11/24/2017
# Author:       Anthem
# Purpose:      To renew Kerberos ticket as it expires every 10 hours
# Location:     /ts/app/ve2/pdp/hpip/phi/no_gbd/r000/bin
#
# Exampleusage: /ts/app/ve2/pdp/hpip/phi/no_gbd/r000/bin/hpip_srvcacct_regen_kerb_tckt.sh
#
kinit srcpdphpipbthts@DEVAD.WELLPOINT.COM -kt /home/srcpdphpipbthts/srcpdphpipbthts.keytab 
