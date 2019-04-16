from flask import ( redirect, url_for,Flask,send_file, render_template,request)
import os
import yaml
import constants
# Create the application instance
app = Flask(__name__, template_folder="templates")

# Step 1 - Load init config
'''
with open('resource/config.yml') as f:
    c = yaml.load(f)
homeValue = c.get('homeValue')
'''
# Create a URL route in our application for "/"
@app.route('/')
def home():
    """
    This function just responds to the browser ULR
    localhost:5000/

    :return:        the rendered templates 'home.html'
    """
    print ("current working directory: {}".format(os.path.abspath(os.path.dirname('./'))))
    return render_template('home.html', path=os.path.abspath(os.path.dirname('./')), value = constants.threshold)

# Step 3 - update global homeValue
@app.route('/update', methods = ['POST'])
def update():
    #global constants.homeValue
    constants.threshold = int(request.form['threshold'])
    return render_template('update.html')

# Step 2 - update homeValue by sending post
@app.route('/updatevalue')
def updatevalue():
    return render_template('update.html')

@app.route('/urlfor')
def hello_user():
    return redirect(url_for('use_url_for'))

@app.route('/useurlfor')
def use_url_for():
    return render_template('url_for.html')




# If we're running in stand alone mode, run the application
if __name__ == '__main__':
    app.run(debug=True)