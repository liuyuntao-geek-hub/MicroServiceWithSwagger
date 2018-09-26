package com.anthem.hca.smartpcp.validator;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;


@GroupSequence({IBlankGroup.class,ISizeGroup.class,IRegexGroup.class,Default.class})
public interface IGroupSequenceForMember {

}
