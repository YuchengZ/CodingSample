from Tkinter import *
from eventBasedAnimationClass import EventBasedAnimationClass
import random
import copy
import math



def drawStop(canvas, x, y, r):
    canvas.create_line(x-r/2.0, y-r/2.0, x-r/2.0, y+r/2.0, width=5)
    canvas.create_line(x+r/2.0, y-r/2.0, x+r/2.0, y+r/2.0, width=5)

def drawBack(canvas, x, y, r):
    r = r/2.0
    canvas.create_polygon((x, y+r, x, y-r, x-r, y))
    canvas.create_polygon((x, y, x+r, y-r, x+r, y+r))

def drawColor(canvas, x, y, r):
    canvas.create_oval(x-r, y-r, x+r, y+r, fill='black', width=0)
    r = r*2.0/3.0
    canvas.create_oval(x-r, y-r, x+r, y+r, fill='yellow', width=0)
    r = r/2.0
    canvas.create_oval(x-r, y-r, x+r, y+r, fill='red', width=0)

def drawBomb(canvas, x, y, r):
    r = r/2.0
    canvas.create_oval(x-r, y-r, x+r, y+r, fill='black', width=0)

def drawLight(canvas, x, y, r):
    canvas.create_line(x+r/2.0, y-r/2.0, x-r/2.0, y+r/2.0, width=5)

def drawStopBall(canvas, x, y, x1, y1, x2, y2, r, text):
    canvas.create_oval(x1, y1, x2, y2)
    drawStop(canvas, x, y, r)
    canvas.create_text(x+2*r, y, text=text, anchor = W, font='Arial 30 bold')

def drawBackBall(canvas, x, y, x1, y1, x2, y2, r, text):
    canvas.create_oval(x1, y1, x2, y2)
    drawBack(canvas, x, y, r)
    canvas.create_text(x+2*r, y, text=text, anchor = W, font='Arial 30 bold')

def drawColorBall(canvas, x, y, x1, y1, x2, y2, r, text):
    rConst = r
    canvas.create_oval(x1, y1, x2, y2)
    canvas.create_oval(x-r, y-r, x+r, y+r, fill='black', width=0)
    r = r*2.0/3.0
    canvas.create_oval(x-r, y-r, x+r, y+r, fill='yellow', width=0)
    r = r/2.0
    canvas.create_oval(x-r, y-r, x+r, y+r, fill='red', width=0)
    r = rConst
    canvas.create_text(x+2*r, y, text=text, anchor = W, font='Arial 30 bold')

def drawBombBall(canvas, x, y, x1, y1, x2, y2, r, text):
    rConst = r
    canvas.create_oval(x1, y1, x2, y2)
    r = r/2.0
    canvas.create_oval(x-r, y-r, x+r, y+r, fill='black', width=0)
    r = rConst
    canvas.create_text(x+2*r, y, text=text, anchor = W, font='Arial 30 bold')

def drawLightBall(canvas, x, y, x1, y1, x2, y2, r, text):
    canvas.create_oval(x1, y1, x2, y2)
    canvas.create_line(x+r/2.0, y-r/2.0, x-r/2.0, y+r/2.0, width=5)
    canvas.create_text(x+2*r, y, text=text, anchor = W, font='Arial 30 bold')


class MovingBall(object):
    def __init__(self, r, color, magic, margin, cellSize, step, pattern):
        self.r = r
        self.color = color
        self.position = -1
        self.margin = margin
        self.cellSize = cellSize
        self.step = step
        self.pattern = pattern
        self.forward = True
        self.back = False
        self.x = 0
        self.y = 0
        self.magic = magic

    def move(self):
        if self.forward:
            self.position += 1

    def moveBack(self):
        if self.back:
            self.position -= 1

    def draw(self, canvas):
        patternPosition = self.position
        row = self.pattern[patternPosition][0]
        col = self.pattern[patternPosition][1]
        self.x = self.margin + col * self.cellSize + self.cellSize/2
        self.y = self.margin + row * self.cellSize + self.cellSize/2
        x = self.x
        y = self.y
        r = self.r
        
        self.image = PhotoImage(file = self.color)
        canvas.create_image(x, y, image=self.image)

        # draw magic sign
        if self.magic == 'stop' :
            drawStop(canvas, x, y, r)
        elif self.magic == 'back':
            drawBack(canvas, x, y, r)
        elif self.magic == 'color' :
            drawColor(canvas, x, y, r)
        elif self.magic == 'bomb' :
            drawBomb(canvas, x, y, r) 
        elif self.magic == 'light':
            drawLight(canvas, x, y, r)


class ShootingBall(object):
    def __init__(self, r, color, magic, speed, cx, cy):
        self.r = r
        self.color = color
        self.speed = speed
        self.cx = cx
        self.cy = cy
        self.x = cx
        self.y = cy
        self.magic = magic

    def shoot(self, cos, sin):
        self.x += self.speed*cos
        self.y += self.speed*sin

    def draw(self, canvas):
        x, y, r = self.x, self.y, self.r
        self.image = PhotoImage(file = self.color)
        canvas.create_image(x, y, image=self.image)
        if self.magic == 'stop' :
            drawStop(canvas, x, y, r)
        elif self.magic == 'back':
            drawBack(canvas, x, y, r)
        elif self.magic == 'color' :
            drawColor(canvas,x, y, r)
        elif self.magic == 'bomb' :
            drawBomb(canvas, x, y, r)
        elif self.magic == 'light':
            drawLight(canvas, x, y, r)

class Property(object):
    def __init__(self, x, y, r, width, height):
        self.setStop(x, y, r, width, height)
        self.setBack(x, y, r, width, height)
        self.setColor(x, y, r, width, height)
        self.setLight(x, y, r, width, height)
        self.setBomb(x, y, r, width, height)
        self.score = 500
        self.imageMoney = PhotoImage(file = 'pictures/money.gif')
        self.r = r

    def setStop(self, x, y, r, width, height):
        self.stop = 1
        self.stopCost = 100
        self.stopX1 = x
        self.stopY1 = y
        self.stopCx = x+r
        self.stopCy = y+r
        self.stopX2 = x+2*r
        self.stopY2 = y+2*r

    def setBack(self, x, y, r, width, height):
        y += height
        self.back = 1
        self.backCost = 200
        self.backX1 = x
        self.backY1 = y
        self.backCx = x+r
        self.backCy = y+r
        self.backX2 = x+2*r
        self.backY2 = y+2*r

    def setColor(self, x, y, r, width, height):
        y += 2*height
        self.color = 1
        self.colorCost = 50
        self.colorX1 = x
        self.colorY1 = y
        self.colorCx = x+r
        self.colorCy = y+r
        self.colorX2 = x+2*r
        self.colorY2 = y+2*r

    def setLight(self, x, y, r, width, height):
        y += 3*height
        self.light = 1
        self.lightCost = 50
        self.lightX1 = x
        self.lightY1 = y
        self.lightCx = x+r
        self.lightCy = y+r
        self.lightX2 = x+2*r
        self.lightY2 = y+2*r

    def setBomb(self, x, y, r, width, height):
        y += 4*height
        self.bomb = 1
        self.bombCost = 300
        self.bombX1 = x
        self.bombY1 = y
        self.bombCx = x+r
        self.bombCy = y+r
        self.bombX2 = x+2*r
        self.bombY2 = y+2*r


    def buyStop(self):
        if self.score>=self.stopCost:
            self.stop+=1
            self.score -= self.stopCost

    def buyBack(self):
        if self.score>=self.backCost:
            self.back+=1
            self.score-=self.backCost

    def buyColor(self):
        if self.score>=self.colorCost:
            self.color+=1
            self.score-=self.colorCost

    def buyLight(self):
        if self.score>=self.lightCost:
            self.light+=1
            self.score-=self.lightCost

    def buyBomb(self):
        if self.score>=self.bombCost:
            self.bomb+=1
            self.score-=self.bombCost

    def drawBackGround(self, canvas):
        x = 680
        y = 90
        self.image=PhotoImage(file='pictures/equipments.gif')
        canvas.create_image(x, y, anchor=NW, image=self.image)


    def drawEquipments(self, canvas):
        self.drawBackGround(canvas)
        r = self.r
        (x1, y1, x2, y2) = (self.stopX1, self.stopY1, self.stopX2, self.stopY2)
        (x, y) = (self.stopCx, self.stopCy)
        drawStopBall(canvas, x, y, x1, y1, x2, y2, r, self.stop)

        (x1, y1, x2, y2) = (self.backX1, self.backY1, self.backX2, self.backY2)
        (x, y) = (self.backCx, self.backCy)
        drawBackBall(canvas, x, y, x1, y1, x2, y2, r, self.back)

        (x1, y1, x2, y2) = (self.colorX1, self.colorY1, 
                            self.colorX2, self.colorY2)
        (x, y) = (self.colorCx, self.colorCy)
        drawColorBall(canvas, x, y, x1, y1, x2, y2, r, self.color)
        
        (x1, y1, x2, y2) = (self.lightX1, self.lightY1, 
                            self.lightX2, self.lightY2)
        (x, y) = (self.lightCx, self.lightCy)
        drawLightBall(canvas, x, y, x1, y1, x2, y2, r, self.light)

        (x1, y1, x2, y2) = (self.bombX1, self.bombY1, self.bombX2, self.bombY2)
        (x, y) = (self.bombCx, self.bombCy)
        drawBombBall(canvas, x, y, x1, y1, x2, y2, r, self.bomb)

    def drawScore(self, canvas, x, y, whith, height):
        canvas.create_image(x, y-8, image=self.imageMoney)
        canvas.create_text(x, y, text= self.score, fill='white', 
                           font='Arial 20 bold')

    def checkStop(self, x, y):
        if (x>self.stopX1 and x<self.stopX2 
                and y>self.stopY1 and y<self.stopY2):
            if self.stop>0:
                self.stop -= 1
                return True
        return False

    def checkBack(self,x, y):
        if (x>self.backX1 and x<self.backX2 
                and y>self.backY1 and y<self.backY2):
            if self.back>0:
                self.back -= 1
                return True
        return False


    def checkColor(self, x, y):
        if (x>self.colorX1 and x<self.colorX2 
                and y>self.colorY1 and y<self.colorY2):
            if self.color>0:
                self.color -= 1
                return True
        return False

    def checkLight(self, x, y):
        if (x>self.lightX1 and x<self.lightX2 
                and y>self.lightY1 and y<self.lightY2):
            if self.light>0:
                self.light -= 1
                return True
        return False

    def checkBomb(self, x, y):
        if (x>self.bombX1 and x<self.bombX2 
                and y>self.bombY1 and y<self.bombY2):
            if self.bomb>0:
                self.bomb -= 1
                return True
        return False

class StoreScreen(object):
    def __init__(self, property):
        self.property = property
        self.returnX1 = 500
        self.returnY1 = 400
        self.returnX2 = 600
        self.returnY2 = 500
        self.setEquipment()
        self.imageStoreScreen = PhotoImage(file = "pictures/storeScreen.gif")
        self.imagePlayButton = PhotoImage(file = "pictures/buttonStart.gif")

    def setEquipment(self):
        self.r = 35
        width = 10
        self.setStop(width)
        self.setBack(width)
        self.setColor(width)
        self.setBomb(width)
        self.setLight(width)

    def setStop(self, width):
        self.stopX1 = 100
        self.stopY1 = 100
        self.stopr = self.r
        self.stopX2 = self.stopX1+2*self.stopr
        self.stopY2 = self.stopY1+2*self.stopr
        self.stop = False

    def setBack(self, width):
        self.backX1 = self.stopX1
        self.backY1 = self.stopY2+width
        self.backr = self.r
        self.backX2 = self.backX1+2*self.backr
        self.backY2 = self.backY1+2*self.backr
        self.back = False

    def setColor(self, width):    
        self.colorX1 = self.stopX1
        self.colorY1 = self.backY2+width
        self.colorr = self.r
        self.colorX2 = self.colorX1+2*self.colorr
        self.colorY2 = self.colorY1+2*self.colorr
        self.color = False

    def setBomb(self, width):
        self.bombX1 = self.stopX1
        self.bombY1 = self.colorY2+width
        self.bombr = self.r
        self.bombX2 = self.bombX1+2*self.bombr
        self.bombY2 = self.bombY1+2*self.bombr
        self.bomb = False

    def setLight(self, width): 
        self.lightX1 = self.stopX1
        self.lightY1 = self.bombY2+width
        self.lightr = self.r
        self.lightX2 = self.lightX1+2*self.lightr
        self.lightY2 = self.lightY1+2*self.lightr
        self.light = False


    def buyEquipments(self, x, y):
        if (x>self.stopX1 and x<self.stopX2 
                and y>self.stopY1 and y<self.stopY2):
            self.property.buyStop()
        elif (x>self.backX1 and x<self.backX2 
                and y>self.backY1 and y<self.backY2):
            self.property.buyBack()

        elif  (x>self.colorX1 and x<self.colorX2 
                and y>self.colorY1 and y<self.colorY2):
            self.property.buyColor()

        elif (x>self.lightX1 and x<self.lightX2 
                and y>self.lightY1 and y<self.lightY2):
            self.property.buyLight()

        elif (x>self.bombX1 and x<self.bombX2 
                and y>self.bombY1 and y<self.bombY2):
            self.property.buyBomb()

    def showEquipments(self, canvas, x, y):
        self.setEquipment()
        self.imagePlayButton = PhotoImage(file = "pictures/buttonStart.gif")
        if (x>self.stopX1 and x<self.stopX2 
                and y>self.stopY1 and y<self.stopY2):
            self.stop = True

        elif (x>self.backX1 and x<self.backX2 
                and y>self.backY1 and y<self.backY2):
            self.back = True

        elif  (x>self.colorX1 and x<self.colorX2 
                and y>self.colorY1 and y<self.colorY2):
            self.color = True

        elif (x>self.lightX1 and x<self.lightX2 
                and y>self.lightY1 and y<self.lightY2):
            self.light = True

        elif (x>self.bombX1 and x<self.bombX2 
                and y>self.bombY1 and y<self.bombY2):
            self.bomb = True

        elif (x>self.returnX1 and x<self.returnX2 
                and y>self.returnY1 and y<self.returnY2):
            self.imagePlayButton=PhotoImage(file = "pictures/buttonStart2.gif")
            


    def draw(self, canvas):
        canvas.create_image(0, 0, anchor = NW, image=self.imageStoreScreen)
        self.drawEquipments(canvas)
        canvas.create_text(400, 200, anchor = NW, 
                           text='Money: %d'%self.property.score, fill='brown', 
                           font='Arial 50 bold')
        (x1, y1, x2, y2) = (self.returnX1, self.returnY1, 
                            self.returnX2, self.returnY2)
        canvas.create_image(x1, y1, anchor = NW, image=self.imagePlayButton)


    def drawEquipments(self, canvas):
        self.drawStop(canvas)
        self.drawBack(canvas)
        self.drawColor(canvas)
        self.drawLight(canvas)
        self.drawBomb(canvas)

    def drawStop(self, canvas):
        r = self.stopr
        (x1, y1, x2, y2) = (self.stopX1, self.stopY1, self.stopX2, self.stopY2)
        (x, y) = ((x1+x2)/2.0, (y1+y2)/2.0)
        if self.stop: 
            canvas.create_rectangle(x1, y1, x2, y2, fill = 'brown', width = 0)
        drawStopBall(canvas, x, y, x1, y1, x2, y2, r, 
                     "cost: %.0f" %self.property.stopCost)
        
    def drawBack(self, canvas):    
        r = self.backr
        (x1, y1, x2, y2) = (self.backX1, self.backY1, self.backX2, self.backY2)
        (x, y) = ((x1+x2)/2.0, (y1+y2)/2.0)
        if self.back: 
            canvas.create_rectangle(x1, y1, x2, y2, fill = 'brown', width = 0)
        drawBackBall(canvas, x, y, x1, y1, x2, y2, r, 
                     "cost: %.0f" %self.property.backCost)

        
    def drawColor(self, canvas):
        r = self.colorr
        (x1, y1, x2, y2) = (self.colorX1, self.colorY1, 
                            self.colorX2, self.colorY2)
        (x, y) = ((x1+x2)/2.0, (y1+y2)/2.0)
        if self.color: 
            canvas.create_rectangle(x1, y1, x2, y2, fill = 'brown', width = 0)
        drawColorBall(canvas, x, y, x1, y1, x2, y2, r,  
                       "cost: %.0f" %self.property.colorCost)
        
        
    def drawLight(self, canvas):  
        r = self.lightr
        (x1, y1, x2, y2) = (self.lightX1, self.lightY1, 
                            self.lightX2, self.lightY2)
        (x, y) = ((x1+x2)/2.0, (y1+y2)/2.0)
        if self.light: 
            canvas.create_rectangle(x1, y1, x2, y2, fill = 'brown', width = 0)
        drawLightBall(canvas, x, y, x1, y1, x2, y2, r,  
                    "cost: %.0f" %self.property.lightCost)

    def drawBomb(self, canvas):    
        r = self.bombr
        (x1, y1, x2, y2) = (self.bombX1, self.bombY1, self.bombX2, self.bombY2)
        (x, y) = ((x1+x2)/2.0, (y1+y2)/2.0)
        if self.bomb: 
            canvas.create_rectangle(x1, y1, x2, y2, fill = 'brown', width = 0)
        drawBombBall(canvas, x, y, x1, y1, x2, y2, r,  
                     "cost: %.0f" %self.property.bombCost)
        
    def clickReturn(self, x, y):
        if (x<self.returnX2 and x>self.returnX1 
            and y<self.returnY2 and y>self.returnY1):
            return True
        return False
        
class HelpScreen(object):
    def __init__(self):
        self.startButtonX = 600
        self.startButtonY = 500
        self.startButtonX1 = 625
        self.startButtonY1 = 530
        self.startButtonWidth = 100
        self.startButtonHeight = 50
        self.startButtonX2 = self.startButtonX1 + self.startButtonWidth
        self.startButtonY2 = self.startButtonY1 + self.startButtonHeight
        self.imageStartButton = PhotoImage(file = "pictures/buttonStart.gif")
        self.imageHelpScreen = PhotoImage(file = "pictures/helpScreen.gif")



    def clickPlay(self, x, y):
        if (x>self.startButtonX1 and x<self.startButtonX2 
                and y>self.startButtonY1 and y<self.startButtonY2):
            return True
        return False

    def showButton(self, x, y):
        self.imageStartButton = PhotoImage(file = "pictures/buttonStart.gif")
        if (x>self.startButtonX1 and x<self.startButtonX2 
                and y>self.startButtonY1 and y<self.startButtonY2):
            self.imageStartButton=PhotoImage(file="pictures/buttonStart2.gif")


    def draw(self, canvas):
        (x, y) = (self.startButtonX, self.startButtonY)
        canvas.create_image(0, 0, anchor = NW, image=self.imageHelpScreen)
        canvas.create_image(x, y, anchor = NW, image=self.imageStartButton)


class Zuma(EventBasedAnimationClass):
    def __init__(self):
        self.music = 0
        self.margin = 60
        self.menu = 200
        self.step = 2
        self.cellSize = 15
        self.ballR = self.cellSize*self.step/2.0
        self.numBalls = 50
        self.rows = 30
        self.cols = 40
        self.shootN = 0
        self.shootMagicNum = 5
        self.shootMagic = None
        super(Zuma, self).__init__(800, 600)
        
#########################################
# Module
#########################################
    def initAnimation(self):
        self.setShowLevel()
        self.setProperty()
        self.setScreen()
        self.setColors()
        self.setPattern()
        self.setHole()
        self.setShootPoint()
        self.setBalls()
        self.setSigns()
        self.setScore()
        self.root.bind("<Motion>", self.onMouseMotion)
        self.root.bind("<Button-2>", self.rightMousePressed)

    def setShowLevel(self):
        self.showLevel2 = False
        self.showDest = False

    def resetAnimation(self):
        self.setColors()
        self.setPattern()
        self.setHole()
        self.setShootPoint()
        self.setBalls()
        self.setSigns()


    def setScreen(self):
        self.setStartScreen()
        self.setGameScreen()
        self.setMapScreen()
        self.setStoreScreen()        
        self.setHelpScreen()
        self.setTimerDelay()

    def setHelpScreen(self):
        self.helpScreen = False 
        self.help = HelpScreen()
    
    def resetScreen(self):
        self.startScreen = False
        self.storeScreen = False
        self.helpScreen = False
        self.mapScreen = False
        self.gameScreen = False
        self.level1 = False
        self.level2 = False

    def setStoreScreen(self):
        self.storeScreen = False
        self.store = StoreScreen(self.property)

    def setMapScreen(self):
        self.imageMapScreen = PhotoImage(file = "pictures/mapScreen.gif")
        self.mapScreen = False
        self.setLevel1()
        self.setLevel2()
        self.setDest()
        self.setStore()

    def setLevel1(self):
        # level1
        self.level1X1 = 130
        self.level1Y1 = 400
        self.level1Width = 100
        self.level1Height = 100
        self.level1X2 = self.level1X1 + self.level1Width
        self.level1Y2 = self.level1Y1 + self.level1Height
        self.imageLevel1 = PhotoImage(file = "pictures/level1.gif")

    def setLevel2(self):
        # level2
        self.level2X1 = 300
        self.level2Y1 = 200
        self.level2Width = 100
        self.level2Height = 100
        self.level2X2 = self.level2X1 + self.level2Width
        self.level2Y2 = self.level2Y1 + self.level2Height
        self.imageLevel2 = PhotoImage(file = "pictures/level2.gif")

    def setDest(self):
        # destination
        self.destX = 600
        self.destY = 100
        self.imageDest = PhotoImage(file='pictures/destination.gif')

    def setStore(self):
        # store
        self.storeX1 = 10
        self.storeY1 = 10
        self.storeWidth = 200
        self.storeHeight = 70
        self.storeX2 = self.storeX1 + self.storeWidth
        self.storeY2 = self.storeY1 + self.storeHeight
        self.imageBuyMore = PhotoImage(file = "pictures/buyMore.gif")


    def setStartScreen(self):
        self.iamgeStartScreen=PhotoImage(file="pictures/startScreenSmall.gif")
        self.startScreen = True
        self.setStart()
        self.setHelp()


    # start button
    def setStart(self):
        self.imageStartButton = PhotoImage(file = "pictures/buttonStart.gif")
        self.startButtonX = 10
        self.startButtonY = 500
        self.startButtonX1 = 25
        self.startButtonY1 = 530
        self.startButtonWidth = 100
        self.startButtonHeight = 50
        self.startButtonX2 = self.startButtonX1 + self.startButtonWidth
        self.startButtonY2 = self.startButtonY1 + self.startButtonHeight
    

    # help button
    def setHelp(self):
        self.imageHelpButton = PhotoImage(file = "pictures/buttonHelp.gif")
        self.startHelpX1 = 660
        self.startHelpY1 = self.startButtonY+10
        self.startHelpWidth = 100
        self.startHelpHeight = 50
        self.startHelpX2 = self.startHelpX1 + self.startHelpWidth
        self.startHelpY2 = self.startHelpY1 + self.startHelpHeight

    def setGameScreen(self):
        self.gameScreen = False
        self.level1 = True
        self.level2 = False
        self.imageLevel = PhotoImage(file='pictures/level.gif')
        self.moneyX = 580
        self.moneyY = self.margin
        self.levelX =200
        self.levelY = self.margin
        self.instructorX = 20
        self.instructorY = 370
        self.imageInstructor = PhotoImage(file='pictures/instructor.gif')


    def setTimerDelay(self):
        if self.gameScreen:
            self.timerDelay = 10
        else:
            self.timerDelay = 100


    def setScore(self):
        self.showScoreThis = False
        self.scoreThisCount = 0
        self.scoreThisCountMax = 15
        self.thisColor = None
        self.scoreX = 700
        self.scoreY = 10
        self.scoreThisX = 0
        self.scoreThisY = 0
        self.collisionX = 0
        self.collisionY = 0
        self.l = 10
        self.scoreThis = 0


    def setProperty(self):
        self.property = Property(700, 180, 20, 50, 50)


    def setSigns(self):
        self.setCount()
        self.setSign()

    def setCount(self):
        self.stepCount = 0
        self.startCount = 0
        self.startCountMax = 70
        self.moveBackCount = 0
        self.moveBackCountMax = 2
        self.moveCount = 0
        self.moveCountMax = 5 
        self.magicStopCount = 0
        self.magicStopCountMax = 100
        self.magicLightCount = 0
        self.magicLightCountMax = 300
        self.magicBackCount = 0
        self.magicBackCountMax = 100

    def setSign(self):
        self.start = True
        self.pause = False
        self.moveBackCollision = False
        self.win = False
        self.gameOver = False
        self.magicStop = False
        self.magicBack = False
        self.magicLight = False
        self.showDialogLost = False


    def setBalls(self):
        n = self.numBalls
        self.balls = []
        colorNum = len(self.colors)
        for i in xrange(n):
            j = random.randint(0,colorNum-1)
            magicNum = random.randint(1, 10)
            if magicNum == 1:
                magic = random.choice(self.magics)
            else: magic = None
            ball = MovingBall(self.ballR, self.colors[j], magic, self.margin,
                              self.cellSize, self.step, self.pattern)
            self.balls+=[ball]
        self.balls[0].position = 0

    def setColors(self):
        self.colors = ['pictures/greenBall.gif','pictures/redBall.gif',
                       'pictures/blueBall.gif', 'pictures/yellowBall.gif']
        self.magics = ['stop', 'stop', 'back', 'color', 'color', 
                       'bomb', 'light', 'light', 'light', 'light']

    def setPattern(self):
        self.pattern = []
        self.patternColor = 'lightGray'
        if self.level1:
            self.setPattern1()
        elif self.level2:
            self.setPattern2()
    
    # pattern for level 1
    def setPattern1(self):
        rows, cols = self.rows, self.cols
        for i in xrange(rows):
            self.pattern += [(i, 0)]
        for i in xrange(1,cols):
            self.pattern += [(rows-1, i)]
        for i in xrange(1, rows-2):
            self.pattern += [(rows-1-i, cols-1)]
        for i in xrange(1, cols-3):
            self.pattern += [(2, cols-1-i)]
        for i in xrange(1, rows-6):
            self.pattern += [(2+i, 3)]
        for i in xrange(1, cols-7):
            self.pattern += [(rows-5, 3+i)]
        for i in xrange(1, rows-11):
            self.pattern += [(rows-5-i, cols-5)]
        for i in xrange(1, cols-12):
            self.pattern += [(7, cols-5-i)]
        # reverse the pattern
        pattern = []
        for i in xrange(len(self.pattern)-1, -1, -1):
            pattern += [self.pattern[i]]
        self.pattern = copy.copy(pattern)
        self.patternLenth = len(self.pattern)

    # pattern for level 2
    def setPattern2(self):
        rows, cols = self.rows, self.cols
        for i in xrange(0, rows-1):
            self.pattern+=[(rows-i, 0)]
        for i in xrange(1, 13):
            self.pattern+=[(2, i)]
        for i in xrange(1,rows):
            self.pattern+=[(i+1, 13)]
        for i in xrange(1, 13):
            self.pattern+=[(rows, 13+i)]
        for i in xrange(1, rows):
            self.pattern+=[(rows-i+1, 26)]
        for i in xrange(1, 12):
            self.pattern+=[(2, 26+i)]
        for i in xrange(1, rows-3):
            self.pattern+=[(i+1, 38)]

    def setHole(self):
        self.holeRow = self.pattern[-1][0]
        self.holeCol = self.pattern[-1][1]
        self.holeR = 20
        self.imageHole = PhotoImage(file = "pictures/hole.gif")

    def setShootPoint(self):
        self.shoot = False
        self.shootSpeed = 20
        self.shootRow = self.rows/2
        self.shootCol = self.cols/2
        self.shootCx = (self.margin + self.shootCol * self.cellSize + 
                        self.cellSize/2)
        self.shootCy = (self.margin + self.shootRow * self.cellSize + 
                        self.cellSize/2)
        self.shootEndx = self.shootCx
        self.shootEndy = self.shootCy
        self.setShootBall()

    def setShootBall(self):
        n=self.shootN
        magicNum = self.shootMagicNum
        magic = self.shootMagic
        self.shootBall = ShootingBall(self.ballR, self.colors[n], magic, 
                         self.shootSpeed, self.shootCx, self.shootCy)
        self.shootN = random.randint(0, len(self.colors)-1)
        self.shootMagicNum = random.randint(1, 10)
        if self.shootMagicNum == 1:
            self.shootMagic = random.choice(self.magics)
        else: self.shootMagic = None
        
#########################################
# Constraller
#########################################
    def onMouseMotion(self,event):
        x, y = event.x, event.y
        if self.startScreen:
            self.mouseMotionStart(x, y)            

        elif self.mapScreen:
            self.mouseMotionMap(x, y)

        elif self.gameScreen:
            self.mouseMotionGame(x, y)

        elif self.storeScreen:
            self.store.showEquipments(self.canvas, x, y)

        elif self.helpScreen:
            self.help.showButton(x, y)

        self.redrawAll()

    def mouseMotionStart(self, x, y):
        if (x>self.startButtonX1 and x<self.startButtonX2 
            and y>self.startButtonY1 and y<self.startButtonY2):
            self.imageStartButton = PhotoImage(file = 
                                               "pictures/buttonStart2.gif")
        elif (x>self.startHelpX1 and x<self.startHelpX2 
            and y>self.startHelpY1 and y<self.startHelpY2):
            self.imageHelpButton = PhotoImage(file="pictures/buttonHelp2.gif")
        else:
            self.imageStartButton = PhotoImage(file="pictures/buttonStart.gif")
            self.imageHelpButton = PhotoImage(file="pictures/buttonHelp.gif")

    def mouseMotionMap(self, x, y):
        if(x>self.storeX1 and x<self.storeX2 
            and y>self.storeY1 and y<self.storeY2):
            self.imageBuyMore = PhotoImage(file = "pictures/buyMore2.gif")
        else:
            self.imageBuyMore = PhotoImage(file = "pictures/buyMore.gif")

    def mouseMotionGame(self, x, y):
        self.shootEndx, self.shootEndy = x, y
        if x!=self.shootCx and y!=self.shootCy:
            self.shootLineCos = ((x-self.shootCx)/((x-self.shootCx)**2.0+
                                 (y-self.shootCy)**2.0)**0.5)
            self.shootLineSin = ((y-self.shootCy)/((x-self.shootCx)**2.0+
                                 (y-self.shootCy)**2.0)**0.5)

    def rightMousePressed(self, event):
        self.setShootPoint()

    def onMousePressed(self, event):
        x, y = event.x, event.y
        if self.startScreen:
            self.mousePressedStart(x, y)

        elif self.mapScreen:
            self.mousePressedMap(x, y)

        elif self.storeScreen:
            self.mousePressedStore(x, y)

        elif self.helpScreen:
            self.mousePressedHelp(x, y)

        elif self.gameScreen:
            self.mousePressedGame(x, y)

    def mousePressedStart(self, x, y):
        if (x>self.startButtonX1 and x<self.startButtonX2 
            and y>self.startButtonY1 and y<self.startButtonY2):
            self.resetScreen()
            self.mapScreen = True
            self.setTimerDelay()

        elif (x>self.startHelpX1 and x<self.startHelpX2 
            and y>self.startHelpY1 and y<self.startHelpY2):
            self.resetScreen()
            self.helpScreen = True
            self.setTimerDelay()

    def mousePressedMap(self, x, y):
        if (x>self.level1X1 and x<self.level1X2 
            and y>self.level1Y1 and y<self.level1Y2):
            self.resetScreen()
            self.gameScreen = True
            self.level1 = True
            self.resetAnimation()
            self.setTimerDelay()

        elif (x>self.level2X1 and x<self.level2X2 
            and y>self.level2Y1 and y<self.level2Y2):
            self.resetScreen()
            self.gameScreen = True
            self.level2 = True
            self.resetAnimation()
            self.setTimerDelay()

        elif (x>self.storeX1 and x<self.storeX2 
            and y>self.storeY1 and y<self.storeY2):
            self.resetScreen()
            self.storeScreen = True
            self.setTimerDelay()

    def mousePressedStore(self, x, y):
        self.store.buyEquipments(x,y)
        if self.store.clickReturn(x, y):
            self.resetScreen()
            self.mapScreen = True
            self.setTimerDelay()       

    def mousePressedHelp(self, x, y):
        if self.help.clickPlay(x, y):
            self.resetScreen()
            self.mapScreen = True
            self.setTimerDelay()

    def mousePressedGame(self, x, y):
        if self.property.checkStop(x, y):
            self.shootBall.magic = 'stop'

        elif self.property.checkBack(x, y):
            self.shootBall.magic = 'back'

        elif self.property.checkColor(x, y):
            self.shootBall.magic = 'color'

        elif self.property.checkLight(x, y):
            self.shootBall.magic = 'light'

        elif self.property.checkBomb(x, y):
            self.shootBall.magic = 'bomb'           

        elif x!=self.shootCx or y!=self.shootCy:
            self.moveShootBall(x, y)

    def onKeyPressed(self, event):
        if event.char == 'q':
            self.pause = True
        elif event.char == 'b':
            self.pause = False
        elif event.char == 'r':
            if self.gameOver:
                self.mapScreen = True
            else:
                self.Animation()
            
    
    # check moving ball and shooting ball collision,
    # if Ture, insertBall and remove balls
    def checkCollision(self):
        index = 0
        for i in xrange(len(self.balls)):
            if self.balls[i].position >= 0:
                if (self.isCollision(self.balls[i], self.shootBall) 
                    and self.balls[0].position<len(self.pattern)-self.step):
                    color = self.shootBall.color
                    self.insertBalls(index)
                    # the index of the all is the collision index+1
                    num = self.hasSameColor(index+1, color)
                    if num>=3:
                        self.moreThanThree(num, color, index)                        
                    break
            index+=1

    def moreThanThree(self, num, color, index):
        self.scoreThis = num*10
        self.thisColor = color
        self.scoreThisX = self.balls[index].x
        self.scoreThisY = self.balls[index].y
        self.collisionX = self.balls[index].x
        self.collisionY = self.balls[index].y
        self.showScoreThis = True
        self.removeBalls(index+1, color)
        self.property.score+=num*10

    def hasSameColor(self, index, color):
        num = 1
        if self.balls[index].magic == 'color':
            addNum = self.hasSameColorMagicColor(index)
            num += addNum
        else:
            num += self.hasSameColorHalf(index-1, color, -1)
            num += self.hasSameColorHalf(index+1, color, +1)
        return num

    def hasSameColorMagicColor(self, index):
        addFront, addBack = False, False
        num = num1 = num2 = 0
        i, j = index+1, index-1
        # looking up
        # find the index that has no magic color
        while(i>=0 and i<len(self.balls) and self.balls[i].magic=='color'):
            i += 1
            num1 +=1
        # if the up part are all magic color
        if i == len(self.balls): addFront = True
        else:
            color1 = self.balls[i].color
            num1 += self.hasSameColorHalf(i, color1, +1)
        # looking down
        # find the index that has no magic color
        while(j>=0 and j<len(self.balls) and self.balls[j].magic=='color'):
            j -= 1
            num2 +=1
        if j == -1: addBack = True
        else:
            color2 = self.balls[j].color
            num2 += self.hasSameColorHalf(j, color2, -1)
        if addFront or addBack or color1==color2:
            if num1+num2>=2: self.setPosition(j, color2)
            return num1+num2
        elif num1 >= num2:
            if num1>=2: self.setPosition(i, color1)
            return num1
        else:
            if num2>=2: self.setPosition(j, color2)
            return num2


    def hasSameColorHalf(self, index, color, step, num=0):
        while (index != len(self.balls) and index != -1
               and (self.balls[index].color == color or 
                self.balls[index].magic == 'color')):
            num+=1
            index = index + step
        return num

    # remove same color balls and reset the movesigns()
    def removeBalls(self, index, color):
        self.setPosition(index, color)
        self.deleteMinusTwo()
        self.setMoveSigns()
    
    # if not connected, set move back sign to the ball need to move back
    def setMoveSigns(self):
        if self.connected():
            pass
        else:
            self.setMoveSignsHelp()

    def connected(self):
        n = self.appearedBalls()
        # if no ball appeared, assume they connected
        if n==0:
            return True
        # if appearedBalls do not have connected position, return False
        for i in xrange(1, n):
            if self.balls[i].position+1*self.step != self.balls[i-1].position:
                return False
        # if last appeared ball's position is not 0, 
        # and the last appeared ball is not the really last ball 
        # then, return False
        if self.balls[n-1].position != 0 and len(self.balls) != n:
            return False
        return True

    def appearedBalls(self):
        num = 0
        for i in xrange(len(self.balls)):
            if self.balls[i].position!=-1:
                num+=1
        return num
    
    # set move back sign need to move back
    def setMoveSignsHelp(self):
        # set the head move back
        forward = True
        back = False
        n = self.appearedBalls()
        for i in xrange((n-1), -1, -1):
            if i == len(self.balls)-1:
                self.balls[i].forward = True
                self.balls[i].back = False
            # check if the line broke when the ball just come out
            elif i==n-1:
                if self.balls[i].position>self.step:
                    self.balls[i+1].position = 0
            elif self.balls[i].position > self.balls[i+1].position+1*self.step:
                if self.balls[i].color == self.balls[i+1].color:
                    forward = False
                    back = True
                else:
                    forward = False
                    back = False
            self.balls[i].forward = forward
            self.balls[i].back = back

    # set the balls'positions to -2 if they need to be removed
    def setPosition(self, index, color):
        if (index == -1 or index == len(self.balls) or 
            (self.balls[index].color!=color and 
            self.balls[index].magic!='color') or
            self.balls[index].position == -2):
            pass
        else:
            self.balls[index].position = -2
            self.checkMagic(index)
            self.setPosition(index+1,color)
            self.setPosition(index-1,color)

    def checkMagic(self, index):
        if self.balls[index].magic == 'stop':
            self.magicStop = True
        elif self.balls[index].magic == 'bomb':
            for i in xrange(7):
                if index+i < len(self.balls):
                    self.balls[index+i].position = -2
                if index-i >= 0:
                    self.balls[index-i].position = -2
        elif self.balls[index].magic == 'back':
            self.magicBack = True
        elif self.balls[index].magic == 'light':
            self.magicLight = True
    
    # deleted the balls have -2 positions
    def deleteMinusTwo(self):
        balls = []
        for i in xrange(len(self.balls)):
            if self.balls[i].position!=-2:
                balls += [self.balls[i]]
        self.balls = copy.copy(balls)

    # return true if shooting ball and moving ball collide
    def isCollision(self, moveBall, shootBall):
        destance = (((moveBall.x-shootBall.x)**2 + 
                    (moveBall.y-shootBall.y)**2)**0.5)
        if destance <= moveBall.r + shootBall.r:
            return True
        return False
    
    # inset shoot ball to the moving balls' queue
    def insertBalls(self, index):       
        position = self.balls[index].position
        # move the front part forword on cell
        self.moveFrontPartForword(index)
        #for i in xrange(index+1):
        #    self.balls[i].position+=1
        newBall = MovingBall(self.ballR, self.shootBall.color, 
                             self.shootBall.magic, self.margin, self.cellSize, 
                             self.step, self.pattern)
        newBall.position = position
        balls = self.balls[:index+1] + [newBall] +self.balls[index+1:]
        self.balls = copy.copy(balls)
        self.setMoveSignsHelp()
        self.setShootPoint()

    def moveFrontPartForword(self, index):
        # if only one ball connected the collision ball,
        # just remove this one
        if (index == 0 or self.balls[index].position <= 
            self.balls[index-1].position-2*self.step):
            self.balls[index].position+=1*self.step
        # move the ball forword until unconnected
        else:
            for i in xrange(index, -1, -1):
                if i == 0:
                    self.balls[i].position+=1*self.step
                elif (self.balls[i].position >= 
                      self.balls[i-1].position-2*self.step):
                    self.balls[i].position+=1*self.step
                else:
                    self.balls[i].position+=1*self.step
                    break


    # set the shooting angle of the shoot ball
    def moveShootBall(self, x, y):
        self.shoot = True
        self.shootCos = ((x-self.shootCx)/((x-self.shootCx)**2.0+
                         (y-self.shootCy)**2.0)**0.5)
        self.shootSin = ((y-self.shootCy)/((x-self.shootCx)**2.0+
                         (y-self.shootCy)**2.0)**0.5)

    def onTimerFired(self):
        self.musicTimer()
        if self.gameScreen:
            if not self.pause:
                if not self.gameOver and not self.win:
                    # deal with move back collision
                    if self.moveBackCollision:
                        self.removeBallsTimer()
                    else:
                        self.moveTimer()
                        self.magicMoveTimer()                        
                        self.checkMoveBallCollision()
                        # move shooting ball
                        self.moveShootTimer()
                        self.checkGameOver()
                        self.checkWin()
                elif self.gameOver:
                    self.gameOverTimer()
                elif self.win:
                    self.winTimer()

    def musicTimer(self):
        self.music+=1
        if self.music == 30000:
            # pygame.mixer.init()
            # pygame.mixer.music.load('music/background.mp3')
            pygame.mixer.music.play()
            self.music = 0

    def removeBallsTimer(self):
        i = self.moveBackIndex
        color = self.balls[self.moveBackIndex].color
        self.removeBalls(i, color)
        self.moveBackCollision = False

    def moveTimer(self):
        # move balls according the move sign and reset the move sign
        # move back first
        self.saveBackSign()
        self.moveBackTimer()
        self.setMoveSignsHelp()
        self.checkMoveBallCollision()
        # move forward then
        self.saveBackSign()
        # stop sign, do not move forward

    def magicMoveTimer(self):
        if self.magicStop:
            self.magicStopTimer()
        # magic Back, move Back
        elif self.magicBack:
            self.magicBackTimer()
        else:
            self.moveForwardTimer()
        # magic light, count
        if self.magicLight:
            self.magicLightTimer()
        self.setMoveSignsHelp()     

    def winTimer(self):
        self.resetScreen()
        self.showScoreThis = False
        self.mapScreen = True
        self.level2 = True
        self.level1 = False
        if not self.showLevel2:
            self.showLevel2 = True
        elif not self.showDest:
            self.showDest = True
        self.setTimerDelay()


    def gameOverTimer(self):
        if(len(self.balls)!=0):
            if self.balls[0].position>=len(self.pattern)-1:
                self.balls.pop(0)
            for i in xrange(len(self.balls)):
                self.balls[i].move()
        else:
            self.showDialogLost = True




    def magicLightTimer(self):
        self.magicLightCount+=1
        if self.magicLightCount>self.magicLightCountMax:
            self.magicLight = False
            self.magicLightCount = 0
  
    def magicBackTimer(self):
        self.magicBackCount+=1
        self.moveCount+=1
        # move
        if self.moveCount>self.moveCountMax:
            self.moveCount = 0
            for i in xrange(len(self.balls)):
                if self.balls[i].position!=-1:
                    self.balls[i].position-=1
        # count
        if self.magicBackCount>self.magicBackCountMax:
            self.magicBack = False
            self.magicBackCount = 0

    def magicStopTimer(self):
        self.magicStopCount+=1
        if self.magicStopCount>self.magicStopCountMax:
            self.magicStop = False
            self.magicStopCount = 0

    def saveBackSign(self):
        self.backSign =[]
        for ball in self.balls:
            self.backSign += [ball.back]

    def checkMoveBallCollision(self):
        if len(self.backSign) == len(self.balls):
            for i in xrange(len(self.balls)-1, -1, -1):
                if self.backSign[i] == True and self.balls[i].back == False:
                    color = self.balls[i].color
                    num = self.hasSameColor(i, color)
                    if num>=3:
                        self.moveBackMoreThanThree(num, color, i)
                    break

    def moveBackMoreThanThree(self, num, color, i):
        self.moveBackCollision == True
        self.moveBackIndex = i
        self.property.score+=num*10
        self.scoreThis = num*10
        self.thisColor = color
        self.scoreThisX = self.balls[i].x
        self.scoreThisY = self.balls[i].y
        self.showScoreThis = True
        self.moveBackCollision = True


    def checkWin(self):
        if len(self.balls) == 0:
            self.win = True

    def checkGameOver(self):
        n = len(self.pattern)
        # if no balls in queue, do not need to check
        if len(self.balls) == 0:
            return
        if self.balls[0].position >= n-1:
            self.gameOver = True
    
    # move seperate part back
    def moveBackTimer(self):
        self.moveBackCount+=1
        if self.moveBackCount>=self.moveBackCountMax:
            for i in xrange(len(self.balls)):
                self.balls[i].moveBack()
                self.moveBackCount = 0
    
    # after reconnected, reset each ball's movement to forward
    def resetMoveForward(self):
        for i in xrange(len(self.balls)):
            self.balls[i].back = False
            self.balls[i].forward = True

    # move forward whole part
    def moveForwardTimer(self):
        # if all the balls removed, do not need to move
        if len(self.balls) == 0:
            return
        else:
            if self.start:
                self.startCount+=1
                self.moveCount+=1
                if self.moveCount>0:
                    if self.balls[0].position < self.patternLenth-1:
                        self.moveBalls()
                        self.moveCount=0
                if self.startCount>self.startCountMax:
                    self.start = False
                    self.startCount = 0
            else:
                self.moveCount+=1
                if self.moveCount>self.moveCountMax:
                    if self.balls[0].position < self.patternLenth-1:
                        self.moveBalls()
                        self.moveCount=0

    # move Shooting ball and check collision
    def moveShootTimer(self):
        if self.shoot:
            self.shootBall.shoot(self.shootCos, self.shootSin)
            self.checkCollision()
            if (self.shootBall.x<0 or self.shootBall.x>self.width
               or self.shootBall.y<0 or self.shootBall.y>self.height):
                self.setShootPoint()


    def moveBalls(self):
        #count = 0
        for i in xrange(len(self.balls)):
            if self.balls[i].position >= 0:
                self.balls[i].move()
               # count+=1
            else: 
                if self.balls[i-1].position==self.step:
                    self.balls[i].position = 0
                    break


#########################################
# view
#########################################
    def redrawAll(self):
        self.canvas.delete(ALL)
        if self.startScreen:
            self.drawStartScreen()
        elif self.helpScreen:
            self.drawHelpScreen()
        elif self.storeScreen:
            self.drawStoreScreen()
        elif self.mapScreen:
            self.drawMapScreen()
        elif self.gameScreen:
            self.drawGameScreen()
            if self.pause:
                self.drawHelpScreenSmall()
            if self.showDialogLost:
                self.drawDialogLost()
            if self.showScoreThis:
                self.drawScoreThis()

    def drawGameScreen(self):
        self.drawBackGround()
        self.drawPattern()
        self.drawHole()
        self.drawScore()
        self.drawLevel()
        self.drawInstructor()
        self.drawEquipments()
        self.drawMovingBalls()
        self.drawShootBall()
        self.drawNextShootBall()
        self.drawShootLine()

    def drawNextShootBall(self):
        self.image=PhotoImage(file= self.colors[self.shootN])
        textX, textY = 360, 55
        self.canvas.create_text(textX, textY, text='Next Shoot: ', 
                                fill = 'brown',font='Arial 20 bold')
        x, y, r = 440, 55, self.ballR
        self.canvas.create_image(x, y, image = self.image)
        if self.shootMagic == 'stop' :
            drawStop(self.canvas, x, y, r)
        elif self.shootMagic == 'back':
            drawBack(self.canvas, x, y, r)
        elif self.shootMagic == 'color' :
            drawColor(self.canvas, x, y, r)
        elif self.shootMagic == 'bomb' :
            drawBomb(self.canvas, x, y, r) 
        elif self.shootMagic == 'light':
            drawLight(self.canvas, x, y, r)


    def drawHelpScreenSmall(self):
        x, y = self.width/2, self.height/2
        self.image = PhotoImage(file='pictures/helpScreenSmall.gif')
        self.canvas.create_image(x, y, image=self.image)


    def drawDialogWin(self):
        self.imageDialog = PhotoImage(file='pictures/dialogWin.gif')
        x, y = 550, 500
        self.canvas.create_image(x, y, image = self.imageDialog)

    def drawDialogLost(self):       
        self.imageDialog = PhotoImage(file ="pictures/dialog.gif")
        x, y = self.width/2, self.height/2
        self.canvas.create_image(x, y, image = self.imageDialog)

    def drawBackGround(self):
        self.drawSquare(0, 0, 800, 600, 'gray')

    def drawInstructor(self):
        x, y = self.instructorX, self.instructorY
        self.canvas.create_image(x, y, anchor=NW, image=self.imageInstructor)

    def drawScoreThis(self):
        x, y = self.scoreThisX, self.scoreThisY
        collisionX, collisionY = self.collisionX, self.collisionY
        self.drawRemoveAnimation(collisionX, collisionY)
        self.canvas.create_text(x, y, text = "+%d" %self.scoreThis, 
                                font='Arial 30 bold')
        self.scoreThisCount+=1
        self.scoreThisY-=3
        
        if self.scoreThisCount>self.scoreThisCountMax:
            self.showScoreThis = False
            self.scoreThisCount = 0
            self.l = 10

    def drawRemoveAnimation(self, x, y):
        if 'green' in self.thisColor:
            self.drawAnimation(x, y, 'green')
        elif 'yellow' in self.thisColor:
            self.drawAnimation(x, y, 'yellow')
        elif 'red' in self.thisColor:
            self.drawAnimation(x, y, 'red')
        elif 'blue' in self.thisColor:
            self.drawAnimation(x, y, 'blue')

    def drawAnimation(self, x, y, color):
        l = self.l
        for i in xrange (0, 360, 10):
            angle = math.radians(i)
            self.canvas.create_line(x, y, x+l*math.cos(angle), y+l*math.sin(angle), fill=color)
        self.l += 5

    def drawEquipments(self):
        self.property.drawEquipments(self.canvas)

    def drawScore(self):
        self.property.drawScore(self.canvas, self.moneyX, self.moneyY, 100, 50)

    def drawLevel(self):
        x, y = self.levelX, self.levelY
        self.canvas.create_image(x, y, image=self.imageLevel)
        if self.level2:
            level = 2
        else:
            level = 1
        self.canvas.create_text(x, y, text='Level %d' %level, 
                                font='Arial 20 bold', fill = 'white')


    def drawStartScreen(self):
        
        self.canvas.create_image(0, 0, anchor=NW, image=self.iamgeStartScreen)
        (x, y) = (self.startButtonX, self.startButtonY)
        self.canvas.create_image(x, y, anchor = NW, image=self.imageStartButton)
        (x, y) = (self.startHelpX1, self.startHelpY1)
        self.canvas.create_image(x, y, anchor = NW, image=self.imageHelpButton)

    def drawHelpScreen(self):
        self.help.draw(self.canvas)

    def drawMapScreen(self):
        self.canvas.create_image(0, 0, anchor = NW, image=self.imageMapScreen)
        (x, y) = (self.storeX1, self.storeY1)
        self.canvas.create_image(x, y, anchor = NW, image=self.imageBuyMore)
        (x, y) = (self.level1X1, self.level1Y1)
        self.canvas.create_image(x, y, anchor = NW, image=self.imageLevel1)
        if self.showLevel2:
            (x, y) = (self.level2X1, self.level2Y1)
            self.canvas.create_image(x, y, anchor = NW, image=self.imageLevel2)
            if self.showDest:
                (x, y) = (self.destX, self.destY)
                self.canvas.create_image(x, y, image=self.imageDest)
                self.drawDialogWin()
        
    def drawStoreScreen(self):
        #self.drawSquare(0, 0, 800, 600, 'black')
        self.store.draw(self.canvas)

    def drawShootLine(self):
        if self.magicLight:
            row, col, r= self.shootRow, self.shootCol, self.ballR
            margin, cellSize = self.margin, self.cellSize
            #x1 = self.shootCx
            #y1 = self.shootCy
            x0 = self.shootCx-r*self.shootLineSin
            y0 = self.shootCy+r*self.shootLineCos
            x1 = self.shootCx+r*self.shootLineSin
            y1 = self.shootCy-r*self.shootLineCos
            x2, y2 = self.shootEndx, self.shootEndy
            self.drawLine(x1, y1, x2, y2, 'cyan', 2) 
            self.drawLine(x0, y0, x2, y2, 'cyan', 2)    


    def drawShootBall(self):
        self.shootBall.draw(self.canvas)

    def drawMovingBalls(self):
        for i in xrange(len(self.balls)):
            if self.balls[i].position >= 0:
                self.balls[i].draw(self.canvas)

    def drawBoard(self):
        rows, cols = self.rows, self.cols
        margin, cellSize = self.margin, self.cellSize
        for row in xrange(rows):
            for col in xrange(cols):
                x1 = margin + col*cellSize
                x2 = x1 + cellSize
                y1 = margin + row*cellSize
                y2 = y1 + cellSize
                self.drawSquare(x1, y1, x2, y2, 'white')

    def drawText(self, x, y, text, color, font):
        self.canvas.create_text(x, y, text=text, fill = color, font = font)

    def drawSquare(self, x1, y1, x2, y2, color, width = 0):
        self.canvas.create_rectangle(x1, y1, x2, y2, 
                                     fill = color, width = width)

    def drawPattern(self):
        n = len(self.pattern)
        margin, cellSize = self.margin, self.cellSize
        for i in xrange(n):
            row = self.pattern[i][0]
            col = self.pattern[i][1]
            x1 = margin + col*cellSize
            x2 = x1 + cellSize
            y1 = margin + row*cellSize
            y2 = y1 + cellSize
            self.drawSquare(x1, y1, x2, y2, self.patternColor)

    def drawHole(self):
        row, col, r = self.holeRow, self.holeCol, self.holeR
        margin, cellSize = self.margin, self.cellSize
        x = margin + col * self.cellSize + self.cellSize/2
        y = margin + row * self.cellSize + self.cellSize/2
        self.drawOval(x, y, r, self.patternColor)
        self.canvas.create_image(x, y, image=self.imageHole)

    def drawOval(self, x, y, r, color):
        self.canvas.create_oval(x-r, y-r, x+r, y+r, fill=color, width=0)

    def drawLine(self, x1, y1, x2, y2, color, width=1):
        self.canvas.create_line(x1, y1, x2, y2, fill = color, width=width)

Zuma().run()






