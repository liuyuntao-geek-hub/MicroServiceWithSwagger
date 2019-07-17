from jsonschema import validate
import jsonschema
import json

def main():
        file = open("./sample/dup/sample_dup_payload.txt", "r")
        file1= file.readlines()
        json_schema = open("./config/uber_dup_payload_schema.json", "r")
        schema = json.load(json_schema)
        # print("schema", schema)
        request_number = 0
        #f_valid = open("C:/Users/AG05216/PycharmProjects/ds-cogx-api/sample/um/ValidPayload.txt", "w")
        f_err = open("./sample/dup/ErrorMessages.generated", "w")
        for line in file1:
            data = json.loads(line)
            request_number = request_number + 1
            z = jsonschema.Draft7Validator(schema)
            for error in sorted(z.iter_errors(data), key=str):
                f_err.write("invalid request number:" + str(request_number))
                print("line number",request_number)
                print("test field:", error.schema.keys())
                f_err.write("\n")
                f_err.write(str(error.schema.get('title')))
                f_err.write(":")
                f_err.write(str(error.message))
                f_err.write("\n")
main()
