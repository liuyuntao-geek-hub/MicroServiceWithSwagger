import datetime as dt

from marshmallow import Schema, fields


class ClaimModel():
    def __init__(self, clm_id, clm_line_nbr, mbr_key, clm_amt, clm_eftv_dt):
        self.clm_id = clm_id
        self.clm_line_nbr = clm_line_nbr
        self.mbr_key = mbr_key
        self.clm_amt = clm_amt
        self.clm_eftv_dt = clm_eftv_dt

    def __repr__(self):
        return '<ClaimObject(clm_id={self.clm_id!r},clm_line_nbr={self.clm_line_nbr!r},mbr_key={self.mbr_key!r},clm_amt={self.clm_amt!r},clm_eftv_dt= {self.clm_eftv_dt!r})'.format(self=self)


class ClaimModelSchema(Schema):
    clm_id = fields.Str()
    clm_line_nbr = fields.Number()
    mbr_key = fields.Str()
    clm_amt = fields.Number()
    clm_eftv_dt = fields.Str()