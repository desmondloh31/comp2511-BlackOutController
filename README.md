COMP2511 Assignment: Back in Blackout
Due: 8am Tuesday Week 4 (22nd June 2021)

Value: 15% of course mark

Contents
[[TOC]]

0. Change Log
Fri 4 Jun: Clarified List of Libraries that are allowed.
Sat 5 Jun:
Removed decimal place requirement on positions (just put the doubles as they are)
Fixed up a typo in one of the examples
(Merged into your repositories): TestHelper now doesn't require an exception message to exist (since some exceptions don't include it)
Fixed Test Paths in Spec
Fixed up velocity for one of the examples
Tue 8 Jun:
Clarified activation periods
Simplified order of device connection
Wed 9 Jun:
Submission is now available!
Specified that devices should connect based on lexiographical order
Thurs 10 Jun:
Make Part 3 a little more specific.
Sun 13 Jun:
Give a bit more of a hint to the pseudo code for Task2
Your Private Repository
Is located at https://gitlab.cse.unsw.edu.au/COMP2511/21T2/students/z555555/21T2-cs2511-assignment

Make sure to replace the zID in the header.

Video
Video discussing the assignment / test code

1. Aims
Practice applying a systematic approach to object-oriented design process
Gain experience in implementing an object-oriented program with multiple interacting classes
Gain hands on experience with Java libraries and JSON
2. Preamble and Problem
So much of today's technology uses Global Positioning System (GPS) information, for example, tasks such as tagging photographs with the location where they were taken, guiding drivers with car navigation systems, and even missile control systems. There are currently 31 active GPS satellites orbiting the Earth all together providing near-constant GPS coverage. There are many other types of satellites too, such as the new SpaceX satellites aiming to provide internet access.

In the far future, society has advanced enough that we have started to occupy the rings of Jupiter. However, to remain connected with the rest of the universe they still rely on satellites! Four major corporations exist: SpaceX, Blue Origin, NASA (National Aeronautics and Space Administration), and Soviet Satellites.

Firstly, you will set up a very simplified simulation of the ring and its rotating satellites. Then we’ll be running the simulation (causing the satellites to orbit) and you’ll be responsible for maintaining connections.

You will need to develop, design, and implement an object-oriented approach that utilises concepts such as abstraction, encapsulation, composition, and inheritance as taught in lectures.

A simple example
Let’s assume initially there is a Blue Origin Satellite at height 10,000m above the centre of Jupiter and at θ = 20 (anticlockwise), there are three devices in a (hollow) ring: DeviceA at θ = 30 (anticlockwise), DeviceB at θ = 180 (anticlockwise) and DeviceC at θ = 330 (anticlockwise).

A simple example

DeviceA and DeviceC are able to connect to the satellite as they are in view of the satellite. This is indicated by "possibleConnections":["DeviceA","DeviceC"]" in the following world state (in JSON representation). An activation period of a device indicates the period during which that device tries to establish connections to the available satellites. For this example, we initially assume that each device is dormant (inactive), and therefore activation periods for each device is an empty list in the following world state. Later we will define activation periods and use them in establishing connections.

{
    "currentTime": "00:00",
    "devices": [
        {
            "activationPeriods": [],
            "id": "DeviceA",
            "isConnected": false,
            "position": 30,
            "type": "HandheldDevice"
        },
        {
            "activationPeriods": [],
            "id": "DeviceB",
            "isConnected": false,
            "position": 180,
            "type": "LaptopDevice"
        },
        {
            "activationPeriods": [],
            "id": "DeviceC",
            "isConnected": false,
            "position": 330,
            "type": "DesktopDevice"
        }
    ],
    "satellites": [
        {
            "connections": [],
            "height": 10000,
            "id": "Satellite1",
            "position": 20,
            "possibleConnections": [
                "DeviceA",
                "DeviceC"
            ],
            "type": "BlueOriginSatellite",
            "velocity": 141.66
        }
    ]
}
Now, using the velocity of the satellite (141.66 metres per minute, which at a height of 10,000m makes it have an angular velocity of 141.66 / 10,000 = 0.014 degrees per minute), we can calculate the new position of the satellite after a full day, as shown below in the figure (the satellite has moved 20.4 degrees as per 141.66 / 10000 * 1440, this gives us a new position of 20 + 20.4 = 40.4). In this new position, DeviceA and DeviceC are able to connect to the satellite and DeviceB is not able to connect to the satellite, because it is not in view of the satellite.

NOTE: For the sake of this assignment we will not be worrying about radians at all. You can either visualise it as the fact that the linear velocities already take into account the radian offset (π/180 or 180/π dependent on which way you are converting) or you can just say that we are approximating them to be the same (radians == degrees). It doesn't matter and for this assignment we aren't really caring about the math, we are focusing on your design!

A simple example

The new world state for this position is

{
  "currentTime": "00:00",
  "devices": [
    {
      "activationPeriods": [],
      "id": "DeviceA",
      "isConnected": false,
      "position": 30,
      "type": "HandheldDevice"
    },
    {
      "activationPeriods": [],
      "id": "DeviceB",
      "isConnected": false,
      "position": 180,
      "type": "LaptopDevice"
    },
    {
      "activationPeriods": [],
      "id": "DeviceC",
      "isConnected": false,
      "position": 330,
      "type": "DesktopDevice"
    }
  ],
  "satellites": [
    {
      "connections": [],
      "height": 10000,
      "id": "Satellite1",
      "position": 40.40
      "possibleConnections": ["DeviceA", "DeviceC"],
      "type": "BlueOriginSatellite",
      "velocity": 141.66
    }
  ]
}
Now, let's add the following activation periods:

DeviceA - "activationPeriods": [{ "startTime": "00:00", "endTime": "12:00" }]
DeviceB - "activationPeriods": [{ "startTime": "00:00", "endTime": "04:00" }]
DeviceC - "activationPeriods": [{ "startTime": "07:00", "endTime": "10:40" }]
Given the above activation periods, the world state at current time 03:00 will be as shown below. Please note that the following:

Device A is active at the current time of 03:00, and also in view of the satellite, so there is a connection between Device A and the satellite, represented by a solid grey line.
Device C is not active at the current time of 03:00, and therefore there is no connection between the satellite and Device C.
Device B is active but not in a view of the satellite, so there is no connection for Device B.
A simple example

We then can go forward to the time 10:00. At this point we have the following:

Device A is active at the current time of 10:00, and also in view of the satellite, so there is a connection between Device A and the satellite.
Device C is active at the current time of 10:00, and also in view of the satellite, so there is a connection between Device C and the satellite.
Device B is not active and not in a view of the satellite, so there is no connection for Device B.
A simple example

Then, let's continue once more, and finish out the full day (so the current time is now 00:00). At this point both connections were completed (at different times). The DeviceA connection ended after 12:00 since the device was no longer active. A similar case occurred for DeviceC where the connection ended after 10:40.

The world state here is as follows;

{
  "currentTime": "00:00",
  "devices": [
    {
      "activationPeriods": [{ "endTime": "12:00", "startTime": "00:00" }],
      "id": "DeviceA",
      "isConnected": false,
      "position": 30,
      "type": "HandheldDevice"
    },
    {
      "activationPeriods": [{ "endTime": "04:00", "startTime": "00:00" }],
      "id": "DeviceB",
      "isConnected": false,
      "position": 180,
      "type": "LaptopDevice"
    },
    {
      "activationPeriods": [{ "endTime": "10:40", "startTime": "07:00" }],
      "id": "DeviceC",
      "isConnected": false,
      "position": 330,
      "type": "DesktopDevice"
    }
  ],
  "satellites": [
    {
      "connections": [
        {
          "deviceId": "DeviceA",
          "endTime": "12:01",
          "minutesActive": 719,
          "satelliteId": "Satellite1",
          "startTime": "00:00"
        },
        {
          "deviceId": "DeviceC",
          "endTime": "10:41",
          "minutesActive": 215,
          "satelliteId": "Satellite1",
          "startTime": "07:00"
        }
      ],
      "height": 10000,
      "id": "Satellite1",
      "position": 60.80,
      "possibleConnections": ["DeviceA"],
      "type": "BlueOriginSatellite",
      "velocity": 141.66
    }
  ]
}
Notice how DeviceC has only been active for 215 minutes but despite that has a start / end time that has a duration of 220 minutes (3 hrs and 40 mins = 180 + 40 = 220)! Even DeviceA has only been alive for 719 minutes despite having a duration of 720 minutes! Why the discrepency?

Well, as we'll find below in section 3, each device/satellite has a series of connection properties (amongst others) and in this case handhelds take 1 minute to connect, and desktops take 5 minutes to connect. Satellites can also effect this (for example SpaceX satellites connect instantly to devices, but can only connect to handhelds).

Want to play around with this for a bit? You can use the following link which has this problem entirely setup already for you.

Visualising Simulation

Simulation
A simulation is an incremental process starting with an initial world state, say WorldState_00. We add a specified time interval of 1 minute and calculate the new positions of all the satellites after the minute. We then go and update all the connections accordingly to derive the next world state WorldState_01. Similarly, we derive WorldState_02 from WorldState_01, WorldState_03 from WorldState_02, and so on. This act of feeding a world state into the next forms a sort of state machine, akin to Conway's Game of Life in a way.

WorldState_00 -> WorldState_01 -> WorldState_02 -> … 

3. Requirements 🪐
You will not be solving ANY maths here; we will be providing a small library that solves all the mathematical details of this problem.

There are three tasks:

Implement the 'world state', you'll be adding/moving devices, adding/removing satellites, and printing out where objects are in the world, you WON'T be simulating them yet.
Implement activation periods (where devices try to connect to satellites) and simulating the 'world state' i.e. moving the satellites around, and updating connections accordingly.
Implement special devices.
Assumptions
In this problem, we are going to have to make some assumptions. We will assume that:

We will only look at a single ring.
The ring is hollow.
Its radius is 3000 metres / 3 kilometres (r).
The ring does not rotate.
We will represent all positions through their angle θ.
The satellites orbit around the disk in 2D space.
Devices 🖨️
HandheldDevice – phones, GPS devices, tablets.
Handhelds take 1 minute to connect
LaptopDevice – laptop computers.
Laptops take 2 minutes to connect
DesktopDevice – desktop computers and servers.
Desktops take 5 minutes to connect
Satellites and Properties 🛰️
SpaceXSatellite
Orbits at a speed of 3330 metres per hour
Only connect to handheld devices
Infinite number of connections
Devices connect instantly
BlueOriginSatellite
Orbits at a speed of 8500 metres per hour
Supports all devices
Maximum of 5 laptops and 2 desktops at a time with no limit on handheld devices, however the absolute maximum of all devices at any time is 10.
i.e. you can have 3 laptops, 1 desktop, and 6 handhelds (3 + 1 + 6 = 10) but you can't have 3 laptops, 1 desktop, and 7 handhelds (3 + 1 + 7 = 11).
NasaSatellite
Orbit at a speed of 5100 metres per hour.
Supports all devices
All devices take 10 minutes to connect (regardless of device)
Maximum of 6 devices (of any type)
Once it reaches this maximum, if there is a device attempting to connect in the region [30°, 40°] (inclusive) AND there are devices outside that region connected to the satellite, it will connect to that device and will drop the oldest connection that is outside of that region. If the new device attempting to connect is outside of that region, it won't connect.
SovietSatellite
Oribts at a speed of 6000 meters per hour
Connect to only laptops and desktops, which both take 2 times as long to connect.
Contains orbit correcting software, this means that whenever it goes outside the region of [140°, 190°] it will attempt to correct it's orbit by reversing it's direction to go back inside the region.
Note it can start outside the region, in that event it should choose the direction that gets it there the fastest. As a hint the threshold angle is 345° (to save you some math).
The correction will only apply on the next 'tick' / minute that passes this means that it can briefly go past the boundary. There is a unit test that details this behaviour quite well, so give that a read it's called testSovietSatelliteMovement in Task2ExampleTests.java
Maximum of 9 devices but will always accept new connections by dropping the oldest connection.
As per later in the specification we won't be constructing scenarios where you end up with a connection disconnecting before they can wait their full time to connect. If a device takes 10 minutes to connect they are guaranteed those 10 minutes atleast.
NOTE: All the measurements above are in metres you'll have to convert those velocities into angular velocities before you use them. To save you some googling v = r * ω (where v is linear velocity i.e. metres per minute, ω is angular velocity i.e. degrees per minute, and r is the radius / height of the satellite). All calculations are safe to be done in degrees. For the sake of this assignment we will not be worrying about radians at all. You can either visualise it as the fact that the linear velocities already take into account the radian offset (π/180 or 180/π dependent on which way you are converting) or you can just say that we are approximating them to be the same (radians == degrees). It doesn't matter and for this assignment we aren't really caring about the math, we are focusing on your design!

Visualisation 🎨
Visualisation Tool

These problems can be hard to visualise! We however, have created a very nice visualisation tool, you can see it here. This tool lets you give it any world state (outputted typically by showWorldState but can also be hand crafted) and will let you simulate it in segments of a day at a time, you'll see the satellites moving around, you'll see connections being formed and just the general movement of systems. It runs on a sample solution so you can refer to this for behaviour! You'll notice that in this world satellites actually move pretty slowly (comparative to the real world), this is mainly just so tests are easier to write and it's easier to watch them move around (rather than completing multiple rotations in a day and just zooming around the ring!).

You can also view the world state by clicking the "World State" button as below.

A simple example

By clicking the "<>" Button we can see a different view. This shows us what console commands we would have to run to get it into this state (maybe useful for your testing!) note that these commands set it up into the state prior to running the simulation. There is also an output state at the top. This is for you to compare to.

A simple example

4. Program Structure
File	Path	Description	Should you need to modify this?
Blackout.java	src/unsw/blackout/Blackout.java	Contains one method for each command you need to implement.	Yes.
Cli.java	src/unsw/blackout/Cli.java	Runs a command line interface for the problem.	No.
MathsHelper.java	src/unsw/blackout/MathsHelper.java	Contains all the math logic that you'll require (satelliteIsVisibleFromDevice).	No.
Task1ExampleTests.java	src/test/Task1ExampleTests.java	Contains a simple test to get you started with Task 1.	Yes, feel free to add more tests here or just create a new testing file.
Task2ExampleTests.java	src/test/Task2ExampleTests.java	Contains a simple test to get you started with Task 2.	Yes, feel free to add more tests here or just create a new testing file.
TestHelper.java	src/test/test_helpers/TestHelper.java	Builds a unit test case and then executes it for you. Verifying output.	No.
ResponseHelper.java	src/test/test_helpers/ResponseHelper.java	Builds an expected response for a given test case.	No.
DummyConnection.java	src/test/test_helpers/DummyConnection.java	Stores data about a connection for the sake of ResponseHelper.	No.
5. Tasks
Task 1 (World State) 🌎
This task is mainly focused on design, if you start at your UML and get that finalised, you'll find this will be quite straightforward! Very little logic exists in this first task.

https://lucid.app/ is a great tool to write UML diagrams and can output a pdf document.

Task 1 a) Create Device
Adds a device to the ring at the position specified, the position is measured in degrees relative to the x-axis, rotating anti-clockwise.

You need to implement the following method createDevice in the file src/unsw/blackout/Blackout.java."

public void createDevice(String id, String type, double position);
Task 1 b) Move Device
Moves a device (specified by id) to a new position (measured in degrees from x axis counter clockwise / anti-clockwise).

You need to implement the following method moveDevice in the file src/unsw/blackout/Blackout.java."

public void moveDevice(String id, double newPosition);
Task 1 b) Remove Device
Removes a device (specified by id).

public void removeDevice(String id);
Task 1 d) Create Satellite
Creates a satellite (specified by id) at a given height (measured from centre of planet, so it’ll include the radius of the ring) at a given position (specified in degrees measured counter clockwise from the x-axis).

public void createSatellite(String id, String type, double height, double position);
Task 1 e) Remove Satellite
Removes a satellite (specified by an id) from the world.

public void removeSatellite(String id);
Task 1 f) Show World State
Lists all satellites and devices that currently exist in the world. Satellites will show their existing connections and devices will show their activationPeriods (note that until Task 2 is completed, these will be an empty array).

The world state will also need to display an array of possibleConnections for each satellite; that is, devices that can connect to the satellite at the current point in time. A device can connect to a satellite if Jupiter is not obstructing the line of sight joining them. We have provided a maths class that does this logic for you - note that it's a static method so you don't need an instance to use it. You call it using MathsHelper.satelliteIsVisibleFromDevice(satelliteAngle, satelliteHeight, deviceAngle).

possibleConnections is PURELY based on whether or not the device is of the right type (i.e. SpaceX can only connect to handhelds) and is within range, it won't incorporate the more complicated sides of this assignment such as capacity/activation periods.
The satellites and devices, ordered by their ID alphabetically. The possibleConnections arrays should be in alphabetical order. The connections for any given satellite should be ordered by startTime, then by deviceId.
Activation periods should be ordered by startTime (you can presume no overlapping activation periods will be given).
Velocities should be outputted in metres per minute.
endTime shouldn't exist in the JSON Object (or be "endTime": null -- note no quotes around null) if the connection hasn't ended yet.
isConnected should be true ONLY if the device is still connected to a satellite.
currentTime should map to the current time of the simulation.
minutesActive should show the current minutes that a connection has been active for, this doesn't include the time for the connection to become activate/connected.
This is a bit unintuitive, so let's just break down an example.
At 10:00 satellites move as usual, now connections are formed; despite the time being 10:00 it's really the 'end' of the minute not the start (i.e. 10:00:59) thus this sort of 'first minute' doesn't count.
After 5 more minutes of movement it's 10:06 (i.e. 10:01, 10:02, 10:03, 10:04, 10:05 where the extra minutes processed ontop of 10:00, so the current time is 10:06)
Now the satellite disconnections (due to whatever reason), the connection ended when the current time is 10:06 sure but in reality it ended just prior at 10:05:59.
Thus the satellite (presuming time to establish/make connection active is 0) would be alive for 5 minutes i.e. from 10:00:59 to 10:05:59, the other way you can think about it is that it's active from 10:00 to 10:05 (recommended you view it this way). This 'weirdness' is a symptom of our low precision (we run our simulations at a precision of a single minute) that makes it hard to calculate this accurately.
public JSONObject showWorldState();
Output

Should return a JSONObject, that looks something like...

{
  "satellites": [
    {
      "id": "Loki",
      "position": 0,
      "velocity": 55.5,
      "possibleConnections": ["LokisPhone"],
      "type": "SpaceXSatellite",
      "connections": [],
      "height": 100000
    }
  ],
  "devices": [
    {
      "id": "GitLabServers",
      "isConnected": false,
      "position": 0.3,
      "activationPeriods": [],
      "type": "DesktopDevice"
    },

    {
      "id": "LokisPhone",
      "isConnected": false,
      "position": 0,
      "activationPeriods": [],
      "type": "HandheldDevice"
    }
  ]
}
NOTE: Highly recommend you look at getting the world state printing earlier rather than later. It's much easier to debug our tests when you have world state working. It'll tell you exactly what properties are missing and what the expected values are.

Task 1 Example
You can test your implementations for Task 1 using the simple test provided in the file src/test/Task1ExampleTests.java. Later you need to add more tests to properly test your implementations.

The method testExample uses a JUnit test to test a few world states. Please read the method testExample, it first creates a new state (using new TestHelper()) and adds three devices (using createDevice) and one satellite (createSatellite) to the state. Later the method showWorldState is called and its output is compared against the expected state (initialWorldState). Similarly, other scenarios (states) are tested using JUnit testing in the given method (such as moving a device).

@Test
public void testExample() {
    // Task 1
    // Example from the specification
    
    // Creates 1 satellite and 3 devices
    // 2 devices are in view of the satellite
    // 1 device is out of view of the satellite
    String initialWorldState = new ResponseHelper(LocalTime.of(0, 0)) 
        // note: all doubles are to 0.01 precision
        // so 141.66 == 141.67.
        .expectSatellite("BlueOriginSatellite", "Satellite1", 10000, 340, 141.66,
            /* Possible Connections */ new String[] { "DeviceA", "DeviceC" })
        .expectDevice("HandheldDevice", "DeviceA", 30)
        .expectDevice("LaptopDevice", "DeviceB", 180)
        .expectDevice("DesktopDevice", "DeviceC", 330)
        .toString();

    // this is what we call the 'builder' pattern, effectively we scope out a test plan in this case
    // by asking the TestHelper to create a series of devices/satellites, then we ask it to show the world state
    // and we use a *different* builder pattern to state how we want the response to look (i.e. expect all the devices/satellites we created before).

    // Since we want you to model your own device/satellite code this is a bit overly 'generic', normally you would probably concrete this a bit
    // i.e. createDevice(new Device(...)) but in this case this is still fine / readable.
    // Unsure what a function does?  Just mouse over it, and you'll get a nice description including sample json output.
    TestHelper plan = new TestHelper()
        .createDevice("HandheldDevice", "DeviceA", 30)
        .createDevice("LaptopDevice", "DeviceB", 180)
        .createDevice("DesktopDevice", "DeviceC", 330)
        .createSatellite("BlueOriginSatellite", "Satellite1", 10000, 340)
        .showWorldState(initialWorldState);

    // Then after moving DeviceA to theta = 211 the world state should be
    String afterMoveWorldState = new ResponseHelper(LocalTime.of(0, 0))
        .expectSatellite("BlueOriginSatellite", "Satellite1", 10000, 340, 141.66, new String[] { "DeviceC" })
        .expectDevice("HandheldDevice", "DeviceA", 211) // position has changed
        .expectDevice("LaptopDevice", "DeviceB", 180)
        .expectDevice("DesktopDevice", "DeviceC", 330)
        .toString();

    plan = plan
        .moveDevice("DeviceA", 211)
        .showWorldState(afterMoveWorldState);
    
    String afterRemovingWorldState = new ResponseHelper(LocalTime.of(0, 0))
        .expectSatellite("BlueOriginSatellite", "Satellite1", 10000, 340, 141.66, new String[] { "DeviceC" })
        .expectDevice("HandheldDevice", "DeviceA", 211)
        .expectDevice("DesktopDevice", "DeviceC", 330)
        // notice no B
        .toString();

    plan = plan
        .removeDevice("DeviceB")
        .showWorldState(afterRemovingWorldState);

    // this is the magic!  DON'T forget this, you need the execute else it won't actually run any tests!
    plan.executeTestPlan();
}
You can also use the provided Cli interface to test your implementations. The following interaction will create three devices and one satellite, and later display output of your showWorldState method. You need to manually check your output with the expected output provided below.

You can run the CLI by running the main method in src/unsw/blackout/Cli.java just clicking the run button above the main method.

> createDevice HandheldDevice DeviceA 30
<
> createDevice LaptopDevice DeviceB 180
<
> createDevice DesktopDevice DeviceC 330
<
> createSatellite BlueOriginSatellite Satellite1 10000 340
<
> showWorldState
< {"satellites":[{"id":"Satellite1","position":340,"velocity":141.66,"possibleConnections":["DeviceA","DeviceC"],
"type":"BlueOriginSatellite","connections":[],"height":10000}],"currentTime":"00:00","devices":[
  {"isConnected":false,"id":"DeviceA","position":30,"activationPeriods":[],"type":"HandheldDevice"},
  {"isConnected":false,"id":"DeviceB","position":180,"activationPeriods":[],"type":"LaptopDevice"},
  {"isConnected":false,"id":"DeviceC","position":330,"activationPeriods":[],"type":"DesktopDevice"}]}
> moveDevice DeviceA 211
< 
> showWorldState
< {"satellites":[{"id":"Satellite1","position":340,"velocity":141.66,"possibleConnections":["DeviceC"],
"type":"BlueOriginSatellite","connections":[],"height":10000}],"currentTime":"00:00","devices":[
  {"isConnected":false,"id":"DeviceA","position":211,"activationPeriods":[],"type":"HandheldDevice"},
  {"isConnected":false,"id":"DeviceB","position":180,"activationPeriods":[],"type":"LaptopDevice"},
  {"isConnected":false,"id":"DeviceC","position":330,"activationPeriods":[],"type":"DesktopDevice"}]}
> removeDevice DeviceB
< 
> showWorldState
< {"satellites":[{"id":"Satellite1","position":340,"velocity":141.66,"possibleConnections":["DeviceC"],
"type":"BlueOriginSatellite","connections":[],"height":10000}],"currentTime":"00:00","devices":[
  {"isConnected":false,"id":"DeviceA","position":211,"activationPeriods":[],"type":"HandheldDevice"},
  {"isConnected":false,"id":"DeviceC","position":330,"activationPeriods":[],"type":"DesktopDevice"}]}
Task 2 (Simulation) 📡
The second tasks involves the actual simluating of the movement of satellites and the scheduling of connections.

Task 2 a) Schedule Device Activation
Activates the specified device for a given period of time.

public void scheduleDeviceActivation(String deviceId, LocalTime start, int durationInMinutes);
Task 2 b) Run the Simulation
You will need to simulate the movement of the satellites and connection of devices to satellites, based on whether the devices are activated and visible. You will need to simulate their movements in increments of 1 minute, and are given the number of minutes to simulate for.

public void simulate(int tickDurationInMinutes);
An example output of this is as follows... you can see the exact sequence of commands that creates this in Task 2 Example (a little further down).

You should use the following pseudocode for updating positions and checking for visibility when running your simulation.

for each step of the simulation:
    update the position of each satellite
    then process disconnections => then connections
HINT: The order of this matters - you want to move every satellite first, then process disconnections (potentially freeing up satellites) then process new connections, it'll make a lot of the logic simpler.

After running the simulate command, the world state should reflect the updated locations of satellites and their existing connections.

Task 2 Example
You can test your implementations for Task 2 using the simple test provided in the file src/test/Task2ExampleTests.java. Later you need to add more tests to properly test your implementations. For Task2 we supply a few different tests just to help you test a variety of cases.

The method testExample uses a JUnit test to test a few world states. Please read the method testExample, it first creates a new state (using new TestHelper()) and adds three devices (using createDevice) and one satellite (createSatellite) to the state. It then runs the simulation for a full day and then the method showWorldState is called and its output is compared against the expected state (afterADay). Similarly, other scenarios (states) are tested using JUnit testing in the given method (such as moving a device).

@Test
public void testExample() {
    // Task 2
    // Example from the specification
    // Creates 1 satellite and 3 devices
    // Activates 2 of the devices and then schedules connections

    String initialWorldState = new ResponseHelper(LocalTime.of(0, 0))
        .expectSatellite("NasaSatellite", "Satellite1", 10000, 340, 85,
                        new String[] { "DeviceA", "DeviceC" })
        .expectDevice("HandheldDevice", "DeviceA", 30, false,
                        new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceB", 180)
        .expectDevice("DesktopDevice", "DeviceC", 330, false,
                        new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(5, 0) } })
        .toString();

    // then simulates for a full day (1440 mins)
    String afterADay = new ResponseHelper(LocalTime.of(0, 0))
        .expectSatellite("NasaSatellite", "Satellite1", 10000, 352.24, 85,
                new String[] { "DeviceA", "DeviceC" },
                new DummyConnection[] {
                    new DummyConnection("DeviceA", LocalTime.of(0, 0), LocalTime.of(6, 41)),
                    new DummyConnection("DeviceC", LocalTime.of(0, 0), LocalTime.of(5, 1)), //
                })
        .expectDevice("HandheldDevice", "DeviceA", 30, false,
                new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(6, 40) } })
        .expectDevice("LaptopDevice", "DeviceB", 180)
        .expectDevice("DesktopDevice", "DeviceC", 330, false,
                new LocalTime[][] { { LocalTime.of(0, 0), LocalTime.of(5, 0) } })
        .toString();

    TestHelper plan = new TestHelper().createDevice("HandheldDevice", "DeviceA", 30)
        .createDevice("LaptopDevice", "DeviceB", 180)
        .createDevice("DesktopDevice", "DeviceC", 330)
        .createSatellite("NasaSatellite", "Satellite1", 10000, 340)
        .scheduleDeviceActivation("DeviceA", LocalTime.of(0, 0), 400)
        .scheduleDeviceActivation("DeviceC", LocalTime.of(0, 0), 300)
        .showWorldState(initialWorldState)
        .simulate(1440)
        .showWorldState(afterADay);
    plan.executeTestPlan();
}
This problem may be easier to visualise using the UI tool. You can do it via the following link. You can also just go to the home page and enter the world state that is listed below.

{
  "currentTime": "00:00",
  "satellites": [
    {
      "id": "Satellite1",
      "position": 340,
      "velocity": 85,
      "possibleConnections": ["DeviceA", "DeviceC"],
      "type": "NasaSatellite",
      "connections": [],
      "height": 10000
    }
  ],
  "devices": [
    {
      "isConnected": false,
      "id": "DeviceA",
      "position": 30,
      "activationPeriods": [{ "startTime": "00:00", "endTime": "06:40" }],
      "type": "HandheldDevice"
    },
    {
      "isConnected": false,
      "id": "DeviceB",
      "position": 180,
      "activationPeriods": [],
      "type": "LaptopDevice"
    },
    {
      "isConnected": false,
      "id": "DeviceC",
      "position": 330,
      "activationPeriods": [{ "startTime": "07:00", "endTime": "12:00" }],
      "type": "DesktopDevice"
    }
  ]
}
Furthermore the CLI commands are as below

> createDevice HandheldDevice DeviceA 30
< 
> createDevice LaptopDevice DeviceB 180
< 
> createDevice DesktopDevice DeviceC 330
< 
> createSatellite NasaSatellite Satellite1 10000 340
< 
> scheduleDeviceActivation DeviceA 00:00 400
< 
> scheduleDeviceActivation DeviceC 07:00 300
< 
> showWorldState
< {"satellites":[{"id":"Satellite1","position":340,"velocity":85,"possibleConnections":["DeviceA","DeviceC"],"type":"NasaSatellite","connections":[],"height":10000}],
"currentTime":"00:00",
"devices":[{"isConnected":false,"id":"DeviceA","position":30,"activationPeriods":[{"startTime":"00:00","endTime":"06:40"}],"type":"HandheldDevice"},{"isConnected":false,"id":"DeviceB","position":180,"activationPeriods":[],"type":"LaptopDevice"},{"isConnected":false,"id":"DeviceC","position":330,"activationPeriods":[{"startTime":"07:00","endTime":"12:00"}],"type":"DesktopDevice"}]}
> simulate 1440
< 
> showWorldState
< {"currentTime":"00:00","devices":[
  {"activationPeriods":[{"endTime":"06:40","startTime":"00:00"}],"id":"DeviceA","isConnected":false,"position":30,"type":"HandheldDevice"},
  {"activationPeriods":[],"id":"DeviceB","isConnected":false,"position":180,"type":"LaptopDevice"},
  {"activationPeriods":[{"endTime":"12:00","startTime":"07:00"}],"id":"DeviceC","isConnected":false,"position":330,"type":"DesktopDevice"}],
  "satellites":[{"connections":[{"deviceId":"DeviceA","endTime":"06:41","minutesActive":390,
  "satelliteId":"Satellite1","startTime":"00:00"},{"deviceId":"DeviceC","endTime":"12:01","minutesActive":290,
  "satelliteId":"Satellite1","startTime":"07:00"}],"height":10000,"id":"Satellite1","position":352.24,"possibleConnections":["DeviceA","DeviceC"],"type":"NasaSatellite","velocity":85}]}
Task 3 (Specialised Devices) 📱
For this task, you will need to add two new specialised types of devices:

(Handheld) MobileX Phones will prioritise connection to a SpaceX satellite if it is in range over any other satellite.

Look for the priority of connection in Other Requirements
(Desktop) AWS (Amazon Web Services) Cloud Servers require 2 connections at any time (to 2 different satellites) if they can only maintain one connection, they will close the other connection.

Scenario 1: For a given 'minute' you can only connect to one satellite => no connections are made!
For the below scenarios you have 2 connections; one to A and another to B which are different satellite ids.

Scenario 2: For a given 'minute' you lose connection to a satellite and can't make any other connections that same minute => both connections are dropped
Scenario 3: For a given 'minute' you lose connection to both satellites, thus they are both dropped.
Scenario 4: For a given 'minute' you lose connection to satellite A but NOT satellite B, and can make a connection to a different satellite, in this case the connection to satellite B remains and you gain a connection to the new satelilte C! The following link shows this off very well (you can see the connections switch when it reaches around 14:00). Please NOTE that satellite B in this case only has one connection the entire time, it was never disconnected since the device could make another connection.
You do NOT have to handle the following for AWS:

Any sort of 'adjustment' to start time / active in minutes to account for the two connections, the connections just have to be established at that point they don't have to be 'active' i.e. you could connect to one satellite for a 5 minute connection time and another for a 10 minute connection time and that's perfectly fine (the 5 minute connection time will be active for 5 minutes before the other one becomes active).
Reconnecting that same minute if AWS has already done it's connection/verification phase that minute. For example let's say you have a NASA satellite, and an AWS Server (called A) as well as 6 other devices. If the AWS server is out of the priority zone for the NASA satellite and thus gets disconnected it only has to worry about reconnection that exact minute if it hasn't already processed it's connections for that minute, otherwise processing them in the next minute is okay.
This is shown via the following simulation
These are created like any other device, specifying "MobileXPhone" and "AWSCloudServer" respectively as the type.

List of Libraries you can use 📚
You can use anything from:

java.io
java.lang
java.math
java.net
java.nio
java.rmi
java.security
java.text
java.time
java.util
org.json
org.junit.jupiter
org.skyscreamer.jsonassert
Most likely however quite a few of these libraries you'll never use.

6. Other Requirements 🔭
You do not need to account for invalid input of any sort (e.g. device/satellite names with spaces, negative heights)
You can presume a device will always finish establishing it's connection. For example if a device takes 10 minutes to connect you can presume the device will remain in range of the satellite for those 10 minutes and the time until it's activation period ends will be atleast 10 minutes.
You can presume no activation periods will overlap
We will never move or remove a device while it is connected to a satellite
We will never remove a satellite while it's connected to devices
We will never give you two satellies that occupy the same position.
When showing the world state, satellites and devices are sorted in alphabetical order
The order of JSON keys within a JSON object doesn't matter i.e. { "a": 1, "b": 2 } is accepted and so is { "b": 2, "a": 1 }. HOWEVER, the order of arrays does matter! Make sure you order them correctly.
The device activation start/end times are inclusive; so if a device is connectable to a satellite and is activated from 10:00 to 10:30, the connection will start at 10:00 and end at 10:30, the duration in minutes would be 30 minutes.
Furthermore, make sure to incorporate the device connection times correctly! Device connection times are specified on both satellites & devices (with various modifiers). Device start times include the time spend connecting but the minutesActive does not for example if it took 10 minutes to connect (in the example of 10:00 to 10:30) the start time would be 10:00, end time would be 10:30 but minutesActive would be 20 (not 30!). Note: MinutesActive should ALWAYS reflect the minutes the connection has been active even if the connection has not yet been disconnected.
ALL device ids are alphanumeric i.e. they consist of just alphabet characters and digits i.e. A-Z, a-z, 0-9, or _.
Radians don't need to be used in this problem, you can keep everything as degrees (for simplicity).
All floating point (double) values only have to be accurate to a precision of 0.01. i.e. 3.33 and 3.34 are both 'equal' in any test we'll be running. You do NOT need to worry about rounding/formatting them in your code just print out the doubles that you have, don't try to use DecimalFormat, it's not needed in this assignment.
Devices disconnect from satellites instantly.
The time it takes for a device to connect to a satellite is not included in the period of time in which it is classified as connected
2 devices cannot occupy the same slot (but there is nothing stopping putting a device at 4.5 degrees and another at 4.51 degrees).
Once connected, devices STAY connected until out of range or the device's activation ends, or the connection is dropped as per specific satellite requirements
All satellites travel anti-clockwise (exception being Soviet Satellites which can travel in both directions) angles are measured from the x-axis, so this means their angle should 'increase' over time.
You should ONLY refer to positions in the range [0, 360) that is any value that is any value >= 360 should be wrapped back around i.e. 360 = 0, 361 = 1, 390 = 30, 720 = 0, ...
Devices connect to satellites based on the following priority (top to bottom).
Prioritise devices based on ID (those with a lexiographically smaller ID get connected first i.e. ABC is before BCD).
If the device is MobileX then it'll prioritise all SpaceX satellites over other ones.
Satellites with a smaller angle are prioritised first.
Yes this does mean that 359 has the lowest priority (handled last) and 0 has the highest priority.
Note: Satellites have to have 'unique' angles this is specified in the specification so you won't have 2 satellites with the same angle.
7. Design 🏛️
You will need to identify entities, attributes and functions within the problem domain and articulate them in a UML class diagram. The UML diagram will need to contain all key elements, including fields, methods, getters and setters, constructors for each entity, inheritance, aggregation and composition relationships and cardinalities.

Put your design in a file named design.pdf in the root directory of this repository.

8. Testing and Dryruns 🧪
The example use cases of Tasks 1 and 2 are set up to run against your code in the src/test directory and are the dryrun for this assignment.

You will need to write your own additional tests. The TestHelper.java and ResponseHelper.java files will allow you to generate tests in the same fashion as the provided ones.

Test testing infrastructure will be asserting what is printed to stdout meaning that you won't be able to debug using normal print statements. You can use System.err.println for debugging print statements instead. The way this works is pretty simple but maybe a bit obscure if you've not taken 1531.

Effectively System.out.println (and printf / friends) all write to a special 'file descriptor' stdout this points to typically some sort of device / handle that will result in the output showing on your terminal. We capture all this stdout output and use it to 'assert' things in the tests, this is quite cool (maybe a bit complex) and definitely a recommendation is to read through TestHelper (you don't need to understand it, but it's useful to atleast grasp why it does what it does). However, this means that if you want debugging printfs you can't use stdout, you instead need to use stderr! This is actually quite a common industry pattern of using stderr's for logs/debug outputs.

9. Style and Documentation 🖌️
You will be assessed on your code style. Examples of things to focus on include:

Correct casing of variable, function and class names;
Meaningful variable and function names;
Readability of code and use of whitespace; and
Modularisation and use of helper functions where needed.
Functions in your classes which are more than simple getters, setters and constructors should be appropriately documented with Javadoc.

10. Tips 💡
No 'logic' should exist in Cli.java you'll almost certainly lose marks for this. It should just be getting parameters from the JSON calling a function from Blackout (or some other class) then wrapping it in an appropriate form then printing it out.
This problem is designed to require inheritance, (more so as the tasks go on). So don't try to avoid it.
You should just need Lists to implement a solution here, if you are reaching for more complicated data structures think about if they are really necessary. i.e. while you could use HashMap's to efficiently lookup satellites/devices the problem doesn't talk about any specific performance requirements and even on Earth there aren't millions of satellites in the sky, there are thousands. Keep it simple, we care about design here not performance, performance is improved by benchmarking then optimisation not just by wildly applying optimisations.
You should NOT store any data as JSONObjects/JSONArrays in your classes, pull the information out of those into ArrayLists/Fields/Classes.
Task 3 is a test of how strong your design is, if you start writing a lot of specific class code in Blackout or similar classes (rather than pushing logic 'into' classes) it is probably an indication that you can cleanup some aspects of your design.
11. Submission 🧳
To submit, run the command:

$ 2511 submit blackout
This requires a UML diagram called design.pdf to be in the 21T2-cs2511-assignment directory (top level). A good tool to draw UMLs is https://lucid.app/ this can export to pdf.

Submission is now made available! Just run the above command in cse and then it runs the two sample tests given. Feel free to submit as you go.

You do NOT need to copy any code across to the CSE servers to submit. Just push your changes to the gitlab, then simply run that submit command via ssh or vlab (or something else connected to cse).

12. Late Penalties
There is a late penalty of 10% per day. The penalty is applied to the maximum mark (ceiling) you can obtain.

You must submit by Friday 5pm, Week 4 or you will receive 0 for this assignment.

13. Marking Criteria ✅
This assignment is out of 40 marks.

Criteria	Description	No. Marks
Correctness - Task 1	Your code will be run against a series of autotests to determine the correctness of your solution. You do not need to have completed Tasks 2/3 to receive full marks for Task 1.	8 marks
Correctness - Task 2	Your code will be run against a series of autotests to determine the correctness of your solution. You do not need to have completed Task 3 to receive full marks for Task 2.	7 marks
Correctness - Task 3	Your code will be run against a series of autotests to determine the correctness of your solution.	5 marks
Design Quality	Your Object Oriented Design and UML diagram will be handmarked on the use of OO principles, abstraction and overall cohesion.	15 marks
Code Quality	Your code quality and style will be handmarked. This includes, but is not limited to:
Commenting of code with Javadoc
Meaningful class, function and variable names
General neatness of code
Use of helper functions and external libraries
3 marks
Testing	Your tests will be assessed on their design and covering a range of possible cases.	2 marks
TOTAL		40 marks
14. Credits 🎥
The premise of this assignment is based on a problem "GPS Blackout" sourced from NCSS Challenge (Advanced), 2016. This assignment has been adapted and modified (quite significantly) from the original problem.
