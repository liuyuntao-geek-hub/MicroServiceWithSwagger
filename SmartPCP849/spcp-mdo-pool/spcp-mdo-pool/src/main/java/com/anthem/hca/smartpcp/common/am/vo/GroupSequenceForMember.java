package com.anthem.hca.smartpcp.common.am.vo;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;


@GroupSequence({BlankGroup.class,SizeGroup.class,RegexGroup.class,Default.class})
public interface GroupSequenceForMember {

}
