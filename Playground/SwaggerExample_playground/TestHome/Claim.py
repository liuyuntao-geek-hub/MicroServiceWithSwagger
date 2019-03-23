from marshmallow import post_load

from SwaggerExample.TestHome.ClaimModel import ClaimModel, ClaimModelSchema



class Claim(ClaimModel):
  def __init__(self, clm_id, clm_line_nbr, mbr_key, clm_amt, clm_eftv_dt):
    super(Claim, self).__init__(clm_id, clm_line_nbr, mbr_key, clm_amt, clm_eftv_dt)

  def __repr__(self):
    return '<Claim(clm_id={self.clm_id!r},clm_line_nbr={self.clm_line_nbr!r},mbr_key={self.mbr_key!r},clm_amt={self.clm_amt!r},clm_eftv_dt= {self.mbr_key!r})'.format(self=self)


class ClaimSchema(ClaimModelSchema):
  @post_load
  def make_claim(self, data):
    return Claim(**data)