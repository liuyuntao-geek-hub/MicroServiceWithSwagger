import unittest
from SwaggerExample.simple import TestingFunc
from SwaggerExample.simple import HelloWorld
import os

import logging.config
logging.config.fileConfig(fname='SwaggerExample/ExternalLogger/file.conf', disable_existing_loggers=False)
logger = logging.getLogger('sLogger')

class TestStringMethods(unittest.TestCase):

    def test_upper(self):
        self.assertEqual('foo'.upper(), 'FOO')

    def test_isupper(self):
        self.assertTrue('FOO'.isupper())
        self.assertFalse('Foo'.isupper())

    def test_split(self):
        s = 'hello world'
        self.assertEqual(s.split(), ['hello', 'world'])
        # check that s.split fails when the separator is not a string
        with self.assertRaises(TypeError):
            s.split(2)

    def test_multi(self):
        self.assertTrue(TestingFunc(2,4).getMultiple() == 8)
    def test_get(self):
        logger.info ('Testing of SimpleGet function Start .... ')
        print (os.getcwd())
        print (HelloWorld().get())
        logger.info('Testing of SimpleGet function end .... ')
        self.assertTrue(True)

if __name__ == '__main__':
    unittest.main()