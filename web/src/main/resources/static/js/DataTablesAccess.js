// put jquery and datatables import before import this file

class DTAccess{

    constructor(datatables,row_prefix='row_',column_len){
        this.datatables = datatables;
        this.row_prefix = row_prefix;
        this.column_len = column_len;
    }

    getRowById(id){
        return $('#'+this.row_prefix+id);
    }

    getColByRowId(id,col_name){
        return this.getRowById(id).find('td[col=\"'+col_name+'\"]');
    }

    getAll(){
        return this.datatables.rows().data();
    }

    getPrefixLessId(prefixedId){
        return prefixedId.slice(this.row_prefix.length);
    }

    removeRowById(id){
        this.datatables.row('#'+this.row_prefix+id).remove().draw();
    }

    setColValueByRowId(id,col_name,new_value){
        let col = this.getColByRowId(id,col_name);

        let target = this.datatables.cell(col);
        target.data(new_value).draw();
    }

    addData(id,data,columns_name){
        if(columns_name.length > this.column_len){
            throw "columns name count can't be greater than "+this.column_len
        }
        this.datatables.row.add(data)
            .draw()
            .node().id = this.row_prefix + id;

        let target = this.getRowById(id);
        for(let i = 0; i < this.column_len; i++){
            target.find('td:eq('+i+')').attr('col',columns_name[i]);
        }
    }

    getSelectedData(){
        return this.datatables.rows({selected:true}).data();
    }

    deselectAll(){
        this.datatables.rows({selected:true}).deselect();
    }

    enableSelection(){
        this.datatables.select.style('multi');
    }

    disableSelection(){
        this.datatables.select.style('api');
    }

    deselectRowById(id){
        this.datatables.row('#'+this.row_prefix+id).deselect();
    }

    static generateColumnSpec(listColumn){
        let len = listColumn.length;
        let result = {};
        result.column = [];
        result.columnDefs = [];

        for(let i = 0; i < len; i++){
            let tmp = {name : listColumn[i]['col'], mData : listColumn[i]['col'] , data : listColumn[i]['col']};
            for(let key in listColumn[i]) {
                if(key !== 'col'){
                    tmp[key] = listColumn[i][key];
                }
            }
            result.column.push(tmp);
            result.columnDefs.push({
                targets : i,
                createdCell : function (td) {
                    td.setAttribute('col',listColumn[i]['col']);
                }
            });
        }

        return result;
    }

}
