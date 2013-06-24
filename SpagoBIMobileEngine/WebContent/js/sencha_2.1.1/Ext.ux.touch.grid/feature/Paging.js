Ext.define('Ext.ux.touch.grid.feature.Paging', {
    extend   : 'Ext.ux.touch.grid.feature.Abstract',
    requires : [
        'Ext.ux.touch.grid.feature.Abstract',
        'Ext.Toolbar',
        'Ext.Button',
        'Ext.Panel'
    ],

    config : {
        events : {
            store : {
                load : 'handleGridPaint'
            }
        },

        extraCls : 'paging',

        goToButton    : {
            disabled : true,
            text     : 'Go to page...'
        },
        backButton    : {
            disabled : true,
            text     : 'Previous Page',
            ui       : 'back'
        },
        forwardButton : {
            disabled : true,
            text     : 'Next Page',
            ui       : 'forward'
        },

        pager         : {
            docked : 'bottom'
        },

        pageListTpl : 'Page {page}',
        goToPicker  : {
            centered      : true,
            modal         : true,
            width         : 200,
            height        : 200,
            layout        : 'fit',
            hideOnMaskTap : true
        },
        pages       : 0
    },

    backText : 'back',

    init : function(grid) {
        var store = grid.getStore();

        if (!store.isLoading()) {
            grid.on('painted', 'handleGridPaint', this, { buffer : 50 });
        }
    },

    applyGoToPicker : function(config, oldConfig) {
        if (!config.hasOwnProperty('items')) {
            config.items = [
                {
                    xtype   : 'list',
                    itemTpl : this.getPageListTpl(),
                    store   : new Ext.data.Store({
                        fields : [
                            'page'
                        ]
                    }),
                    listeners : {
                        scope   : this,
                        itemtap : 'handlePageSelect'
                    }
                }
            ];
        }

        return Ext.factory(config, Ext.Panel, oldConfig);
    },

    applyPager : function(newPager, oldPager) {
        return Ext.factory(newPager, Ext.Toolbar, oldPager);
    },

    updatePager : function(newPager, oldPager) {
        var me   = this,
            grid = me.getGrid();

        if (oldPager) {
            grid.remove(oldPager);
        }

        if (newPager) {
            grid.insert(0, newPager);
        }
    },

    applyBackButton : function(config, oldButton) {
        Ext.apply(config, {
            action   : 'back',
            disabled : true,
            scope    : this,
            handler  : 'handleBackButton'
        });

        return Ext.factory(config, Ext.Button, oldButton);
    },

    applyGoToButton : function(config, oldButton) {
        Ext.apply(config, {
            action   : 'goTo',
            disabled : true,
            scope    : this,
            handler  : 'handleGoToButton'
        });

        return Ext.factory(config, Ext.Button, oldButton);
    },

    applyForwardButton : function(config, oldButton) {
        Ext.apply(config, {
            action   : 'forward',
            disabled : true,
            scope    : this,
            handler  : 'handleForwardButton'
        });

        return Ext.factory(config, Ext.Button, oldButton);
    },

    updateBackButton : function(newButton, oldButton) {
        var me    = this,
            pager = me.getPager(),
            idx   = 0;

        if (oldButton) {
            idx = pager.getInnerItems().indexOf(oldButton);

            pager.remove(oldButton);
        }

        if (newButton) {
            pager.insert(idx, newButton);
            me.checkSpacers();
        }
    },

    updateGoToButton : function(newButton, oldButton) {
        var me    = this,
            pager = me.getPager(),
            idx   = 2;

        if (oldButton) {
            idx = pager.getInnerItems().indexOf(oldButton);

            pager.remove(oldButton);
        }

        if (newButton) {
            pager.insert(idx, newButton);
            me.checkSpacers();
        }
    },

    updateForwardButton : function(newButton, oldButton) {
        var me    = this,
            pager = me.getPager(),
            idx   = 4;

        if (oldButton) {
            idx = pager.getInnerItems().indexOf(oldButton);

            pager.remove(oldButton);
        }

        if (newButton) {
            pager.insert(idx, newButton);
            me.checkSpacers();
        }
    },

    checkSpacers : function() {
        var me         = this,
            pager      = this.getPager(),
            items      = pager.getInnerItems(),
            forwardBtn = me.getForwardButton(),
            goToBtn    = me.getGoToButton(),
            idx, spacer;

        if (forwardBtn) {
            idx = items.indexOf(forwardBtn);

            if (idx > 0) {
                spacer = items[idx - 1];

                if (!spacer.isXType('spacer')) {
                    pager.insert(idx, {
                        xtype : 'spacer'
                    });
                }
            }
        }

        if (goToBtn) {
            idx = items.indexOf(goToBtn);

            if (idx > 0) {
                spacer = items[idx - 1];

                if (!spacer.isXType('spacer')) {
                    pager.insert(idx, {
                        xtype : 'spacer'
                    });
                }
            }
        }
    },

    handleGridPaint : function(grid) {
        if (!(grid instanceof Ext.ux.touch.grid.List)) {
            grid = this.getGrid();
        }

        var me    = this,
            store = grid.getStore();

        if (store.isLoading()) {
            store.on('load', 'handleGridPaint', me, { single : true });
            return;
        }
        store.on('clear', 'handleGridPaint', me, { single : true });

        var total         = store.getTotalCount(),
            currentPage   = store.currentPage,
            pages         = Math.ceil(total / store.getPageSize()),
            backButton    = me.getBackButton(),
            forwardButton = me.getForwardButton(),
            goToButton    = me.getGoToButton();

        me.setPages(pages);

        backButton   .setDisabled(currentPage === 1 || store.getCount() === 0);
        forwardButton.setDisabled(currentPage === pages || store.getCount() === 0);
        goToButton   .setDisabled(pages === 0  || store.getCount() === 0);
    },

    handleBackButton : function() {
        var grid  = this.getGrid(),
            store = grid.getStore();

        store.previousPage();
    },

    handleForwardButton : function() {
        var grid  = this.getGrid(),
            store = grid.getStore();

        store.nextPage();
    },

    handleGoToButton : function(btn) {
        var me     = this,
            picker = me.getGoToPicker(),
            pages  = me.getPages(),
            i      = 1,
            data   = [];

        var store = picker.down('list').getStore();

        store.removeAll();

        for (; i <= pages; i++) {
            data.push({ page : i });
        }

        store.add(data);

        picker.showBy(btn);
    },

    handlePageSelect : function(list, index) {
        var panel = list.up('panel'),
            store = this.getGrid().getStore(),
            page  = index + 1;

        store.loadPage(page);

        panel.hide();
    }
});