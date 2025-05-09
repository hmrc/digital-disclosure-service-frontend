#!/bin/bash

sbt clean scalafmt test:scalafmt coverage test it/test:test coverageReport
