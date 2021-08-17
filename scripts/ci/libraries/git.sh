#!/usr/bin/env bash

function git::get_last_tag() {
    echo $(git describe --tags `git rev-list --tags --max-count=1`)
}
